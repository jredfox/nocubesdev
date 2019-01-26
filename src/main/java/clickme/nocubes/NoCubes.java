package clickme.nocubes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import clickme.nocubes.gui.GuiCubeSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;

@Mod(modid = "nocubes", name = "No Cubes", version = "0.8")
public class NoCubes
{
    public static final String MOD_ID = "noCubes";
    public static final String VERSION = "0.8";
    public static SoftBlockRenderer softBlockRenderer;
    private static List softBlockList = new ArrayList<Block>();
    private static List liquidBlockList;
    private static List leavesBlockList;
    public static KeyBinding keyOpenSettings;
    public static GuiScreen optionGui;
    private static Configuration cubesConfig;
    public static boolean isNoCubesEnabled;
    public static boolean isAutoStepEnabled;
    protected static List<String> list = new ArrayList<String>();
    protected static List<Block> ListBlack = new ArrayList<Block>();
    protected static boolean isblackListing = false;
    protected static boolean autoDetection = true;
    protected static boolean autoDirt,autoStone,autoGravel,autoLeaves,autoSnow,autoLiquid,autoIce = true;
    

    public NoCubes()
    {
    	
    }

    @Mod.EventHandler
    public void preInitialization(FMLPreInitializationEvent event)
    {
        if(event.getSide() != Side.CLIENT)
            return;
        NoCubes.keyOpenSettings = new KeyBinding("key.noCubes", 24, "key.noCubes");
        NoCubes.optionGui = new GuiCubeSettings();
        ClientRegistry.registerKeyBinding(NoCubes.keyOpenSettings);
        (NoCubes.cubesConfig = new Configuration(event.getSuggestedConfigurationFile())).load();
        NoCubes.autoDetection = NoCubes.cubesConfig.get("general", "AutoDetection", true).getBoolean(true);
        NoCubes.isNoCubesEnabled = NoCubes.cubesConfig.get("general", "EnableNoCubes", true).getBoolean(true);
        NoCubes.isAutoStepEnabled = NoCubes.cubesConfig.get("general", "EnableAutoStep", true).getBoolean(true);
        NoCubes.cubesConfig.save();
        FMLCommonHandler.instance().bus().register((Object)new ForgeEventHandler(this));
    }
    @Mod.EventHandler
    public void postInitialization(FMLPostInitializationEvent event)
    {
    	 if(event.getSide() != Side.CLIENT)
             return;
    	 NoCubes.cubesConfig.load();
        loadBlocks();
        loadBlocksBlackList();
        if(autoDetection)
           loadAutoDetection();
        NoCubes.cubesConfig.save();
    }
    public static void loadAutoDetection()
    {
    	NoCubes.autoDirt = NoCubes.cubesConfig.get("general", "autoDirt", true).getBoolean(true);
    	NoCubes.autoGravel = NoCubes.cubesConfig.get("general", "autoGravel_and_Sand", true).getBoolean(true);
    	NoCubes.autoLeaves = NoCubes.cubesConfig.get("general", "autoLeaves", true).getBoolean(true);
    	NoCubes.autoStone = NoCubes.cubesConfig.get("general", "autoStone", false).getBoolean(false);
    	NoCubes.autoSnow = NoCubes.cubesConfig.get("general", "autoSnow", true).getBoolean(true);
    	NoCubes.autoIce = NoCubes.cubesConfig.get("general", "autoIce", true).getBoolean(true);
    	NoCubes.autoLiquid = NoCubes.cubesConfig.get("general", "autoLiquid", true).getBoolean(true);
    	String[] s = {"examplemod:stone"};
    	String[] strautoblacklist = NoCubes.cubesConfig.getStringList("Auto Detection BlackList", "blocks", s, "Add Your Blocks Here");
    	ArrayList<Block> autoblacklist = new ArrayList<Block>();
    	for(String str : strautoblacklist)
    		autoblacklist.add(createBlock(str));
    	Iterator<Block> it = ForgeRegistries.BLOCKS.iterator();
    	while(it.hasNext())
    	{
    		Block block = it.next();
    		IBlockState state = block.getDefaultState();
    		Material mat = block.getMaterial(state);
    		boolean isOpaqueCube = block.isOpaqueCube(state);
    		if(mat != Material.PLANTS && !autoblacklist.contains(block))
    		{
    		 System.out.println(mat.toString());
    		if(autoDirt && mat == Material.GROUND && isOpaqueCube || autoDirt && mat == Material.GRASS && isOpaqueCube)
    			NoCubes.softBlockList.add(block);
    		if(autoGravel && mat == Material.SAND && isOpaqueCube)
    			NoCubes.softBlockList.add(block);
    		if(autoStone && mat == Material.ROCK && isOpaqueCube)
    			NoCubes.softBlockList.add(block);
    		if(autoSnow && mat == Material.SNOW || autoSnow && mat == Material.CRAFTED_SNOW)
    			NoCubes.softBlockList.add(block);
    		if(autoIce && mat == Material.ICE ||autoIce && mat == Material.PACKED_ICE)
    			NoCubes.softBlockList.add(block);
    		
    		//Important AutoDectections
    		if(autoLeaves && mat == Material.LEAVES)
    			NoCubes.leavesBlockList.add(block);
    		if(autoLiquid && mat == Material.LAVA || autoLiquid && mat == Material.WATER || autoLiquid && block instanceof BlockLiquid)
    			NoCubes.liquidBlockList.add(block);
    		}
    	}
    }

    public static void saveCubeConfig()
    {
        NoCubes.cubesConfig.load();
        NoCubes.cubesConfig.get("general", "EnableNoCubes", true).set(NoCubes.isNoCubesEnabled);
        NoCubes.cubesConfig.get("general", "EnableAutoStep", true).set(NoCubes.isAutoStepEnabled);
        NoCubes.cubesConfig.save();
    }
    public static void loadBlocks()
    {
        String[] s = new String[20];
        s[0] = "minecraft:grass";
        s[1] = "minecraft:dirt";
        s[2] = "minecraft:sand";
        s[3] = "minecraft:gravel";
        s[4] = "minecraft:clay";
        s[5] = "minecraft:farmland";
        s[6] = "minecraft:mycelium";
        s[7] = "minecraft:snow_layer";
        s[8] = "minecraft:stone";
        s[9] = "minecraft:coal_ore";
        s[10] = "minecraft:iron_ore";
        s[11] = "minecraft:gold_ore";
        s[12] = "minecraft:diamond_ore";
        s[13] = "minecraft:redstone_ore";
        s[14] = "minecraft:emerald_ore";
        s[15] = "minecraft:bedrock";
        s[16] = "minecraft:netherrack";
        s[17] = "minecraft:soul_sand";
        s[18] = "minecraft:soul_sand";
        s[19] = "minecraft:end_stone";
        
        String[] whitelist = NoCubes.cubesConfig.getStringList("No Cubes List", "blocks", s, "Add Your Blocks Here");
        isblackListing = NoCubes.cubesConfig.getBoolean("blacklist_mode", "blocks", false, "All blocks Except in Blacklist Mode will work");
        for(String ss : whitelist)
        {
        	softBlockList.add(createBlock(ss));
    	if(ss.equals("minecraft:redstone_ore"))
    		softBlockList.add(Blocks.LIT_REDSTONE_ORE);
        }
    }
    public static void loadBlocksBlackList()
    {
    	String[] ss = {"examplemod:wood"};
        String[] blacklist = NoCubes.cubesConfig.getStringList("No Cubes BlackList Mode", "blocks", ss, "BlackList Blocks Here");
    	for(String s : blacklist)
    	{
        	ListBlack.add(createBlock(s));
        	if(s.equals("minecraft:redstone_ore"))
        		ListBlack.add(Blocks.LIT_REDSTONE_ORE);
    	}
    		
    }
    public static Block createBlock(String s)
    {
    	return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
    }

    protected void openCubeSettingsGui()
    {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiCubeSettings());
    }

    public static boolean isBlockSoft(final Block block)
    {
    		if(!isblackListing)
    			return NoCubes.softBlockList.contains(block);
    		else
    			return !ListBlack.contains(block);
    }

    public static boolean isBlockSoftForCollision(final Block block)
    {
        return NoCubes.softBlockList.contains(block) && NoCubes.isAutoStepEnabled;
    }

    public static boolean isBlockLiquid(final Block block)
    {
        return NoCubes.liquidBlockList.contains(block);
    }

    public static void renderBlockSoft(final Block block)
    {
        NoCubes.softBlockList.add(block);
    }

    public static void registerAsLiquid(final Block block)
    {
        NoCubes.liquidBlockList.add(block);
    }

    public static void registerAsLeaves(final Block block)
    {
        NoCubes.leavesBlockList.add(block);
    }

    static
    {
        NoCubes.softBlockRenderer = new SoftBlockRenderer();
        //NoCubes.softBlockList = new ArrayList();
        NoCubes.liquidBlockList = new ArrayList();
        NoCubes.leavesBlockList = new ArrayList();
        /*
        NoCubes.softBlockList.add(Blocks.grass);
        NoCubes.softBlockList.add(Blocks.dirt);
        NoCubes.softBlockList.add(Blocks.sand);
        NoCubes.softBlockList.add(Blocks.gravel);
        NoCubes.softBlockList.add(Blocks.clay);
        NoCubes.softBlockList.add(Blocks.farmland);
        NoCubes.softBlockList.add(Blocks.mycelium);
        NoCubes.softBlockList.add(Blocks.snow_layer);
        NoCubes.softBlockList.add(Blocks.stone);
        NoCubes.softBlockList.add(Blocks.coal_ore);
        NoCubes.softBlockList.add(Blocks.iron_ore);
        NoCubes.softBlockList.add(Blocks.gold_ore);
        NoCubes.softBlockList.add(Blocks.diamond_ore);
        NoCubes.softBlockList.add(Blocks.redstone_ore);
        NoCubes.softBlockList.add(Blocks.lit_redstone_ore);
        NoCubes.softBlockList.add(Blocks.emerald_ore);
        NoCubes.softBlockList.add(Blocks.bedrock);
        NoCubes.softBlockList.add(Blocks.netherrack);
        NoCubes.softBlockList.add(Blocks.soul_sand);
        NoCubes.softBlockList.add(Blocks.quartz_ore);
        NoCubes.softBlockList.add(Blocks.end_stone);
        */
        NoCubes.liquidBlockList.add(Blocks.WATER);
        NoCubes.liquidBlockList.add(Blocks.FLOWING_WATER);
        NoCubes.liquidBlockList.add(Blocks.LAVA);
        NoCubes.liquidBlockList.add(Blocks.FLOWING_LAVA);
        NoCubes.leavesBlockList.add(Blocks.LEAVES);
        NoCubes.leavesBlockList.add(Blocks.LEAVES2);
    }
}

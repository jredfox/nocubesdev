package clickme.nocubes.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions({"clickme.nocubes.asm"})
public class CoreModLoader implements IFMLLoadingPlugin
{
	@Override
    public String[] getASMTransformerClass()
    {
        return new String[] {"clickme.nocubes.asm.BlockTweakInjector", "clickme.nocubes.asm.RenderTweakInjector"};
    }
	@Override
    public String getModContainerClass()
    {
        return null;
    }
	@Override
    public String getSetupClass()
    {
        return null;
    }
	@Override
    public void injectData(final Map<String, Object> data)
    {}
	@Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}

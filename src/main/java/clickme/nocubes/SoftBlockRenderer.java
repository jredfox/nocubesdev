package clickme.nocubes;

import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.block.material.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.*;
import net.minecraft.entity.*;
import clickme.nocubes.test.*;

public class SoftBlockRenderer
{
	public static int getColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos)
	{
    	return BlockColors.init().getColor(state, (World) blockAccess, pos);
	}
    public boolean renderSoftBlock(final Block block, BlockPos pos, final RenderBlocks renderer, final IBlockAccess world)
    {
    	final int x = pos.getX();
    	final int y = pos.getY();
    	final int z = pos.getZ();
        final BufferBuilder tessellator = Tessellator.getInstance().getBuffer();
        final IBlockState state = world.getBlockState(pos);
        final int color = getColor(state,world,pos);
        final float colorRed = (color >> 16 & 0xFF) / 255.0f;
        final float colorGreen = (color >> 8 & 0xFF) / 255.0f;
        final float colorBlue = (color & 0xFF) / 255.0f;
        final float shadowBottom = 0.6f;
        final float shadowTop = 1.0f;
        final float shadowLeft = 0.9f;
        final float shadowRight = 0.8f;
        IIcon icon;
        if(!renderer.hasOverrideBlockTexture())
        {
            icon = renderer.getBlockIconFromSideAndMetadata(block, 1, meta);
        }
        else
        {
            icon = renderer.overrideBlockTexture;
        }
        final double minU = icon.getMinU();
        final double minV = icon.getMinV();
        final double maxU = icon.getMaxU();
        final double maxV = icon.getMaxV();
      
        final Vec3d[] points = {new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(0.0, 1.0, 1.0)};
        for(int point = 0; point < 8; ++point)
        {
            final Vec3d vec3 = points[point];
            vec3.x += x;
            final Vec3d vec4 = points[point];
            vec4.y += y;
            final Vec3d vec5 = points[point];
            vec5.z += z;
            if(!doesPointIntersectWithManufactured(world, points[point]))
            {
                if(point < 4 && doesPointBottomIntersectWithAir(world, points[point]))
                {
                    points[point].y = y + 1.0;
                }
                else if(point >= 4 && doesPointTopIntersectWithAir(world, points[point]))
                {
                    points[point].y = y;
                }
                points[point] = this.givePointRoughness(points[point]);
            }
        }
        for(int side = 0; side < 6; ++side)
        {
            int facingX = x;
            int facingY = y;
            int facingZ = z;
            if(side == 0)
            {
                --facingY;
            }
            else if(side == 1)
            {
                ++facingY;
            }
            else if(side == 2)
            {
                --facingZ;
            }
            else if(side == 3)
            {
                ++facingX;
            }
            else if(side == 4)
            {
                ++facingZ;
            }
            else if(side == 5)
            {
                --facingX;
            }
            if(renderer.renderAllFaces || block.shouldSideBeRendered(world, facingX, facingY, facingZ, side))
            {
                float colorFactor = 1.0f;
                Vec3d vertex0 = null;
                Vec3d vertex2 = null;
                Vec3d vertex3 = null;
                Vec3d vertex4 = null;
                if(side == 0)
                {
                    colorFactor = shadowBottom;
                    vertex0 = points[0];
                    vertex2 = points[1];
                    vertex3 = points[2];
                    vertex4 = points[3];
                }
                else if(side == 1)
                {
                    colorFactor = shadowTop;
                    vertex0 = points[7];
                    vertex2 = points[6];
                    vertex3 = points[5];
                    vertex4 = points[4];
                }
                else if(side == 2)
                {
                    colorFactor = shadowLeft;
                    vertex0 = points[1];
                    vertex2 = points[0];
                    vertex3 = points[4];
                    vertex4 = points[5];
                }
                else if(side == 3)
                {
                    colorFactor = shadowRight;
                    vertex0 = points[2];
                    vertex2 = points[1];
                    vertex3 = points[5];
                    vertex4 = points[6];
                }
                else if(side == 4)
                {
                    colorFactor = shadowLeft;
                    vertex0 = points[3];
                    vertex2 = points[2];
                    vertex3 = points[6];
                    vertex4 = points[7];
                }
                else if(side == 5)
                {
                    colorFactor = shadowRight;
                    vertex0 = points[0];
                    vertex2 = points[3];
                    vertex3 = points[7];
                    vertex4 = points[4];
                }
                
                tessellator.color(shadowTop * colorFactor * colorRed, shadowTop * colorFactor * colorGreen, shadowTop * colorFactor * colorBlue);
                tessellator.pos(vertex0.x, vertex0.y, vertex0.z).tex(minU, maxV);
                tessellator.pos(vertex2.x, vertex2.y, vertex2.z).tex(minU, maxV);
                tessellator.pos(vertex4.x, vertex4.y, vertex4.z).tex(minU, maxV);
                
                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, facingX, facingY, facingZ));
                tessellator.setColorOpaque_F(shadowTop * colorFactor * colorRed, shadowTop * colorFactor * colorGreen, shadowTop * colorFactor * colorBlue);
                tessellator.addVertexWithUV(vertex0.x, vertex0.y, vertex0.z, minU, maxV);
                tessellator.addVertexWithUV(vertex2.x, vertex2.y, vertex2.z, maxU, maxV);
                tessellator.addVertexWithUV(vertex3.x, vertex3.y, vertex3.z, maxU, minV);
                tessellator.addVertexWithUV(vertex4.x, vertex4.y, vertex4.z, minU, minV);
            }
        }
        return true;
    }

    public Vec3d givePointRoughness(final Vec3d point)
    {
        long i = (long)(point.x * 3129871.0) ^ (long)point.y * 116129781L ^ (long)point.z;
        i = i * i * 42317861L + i * 11L;
        point.x += ((i >> 16 & 0xFL) / 15.0f - 0.5f) * 0.5f;
        point.y += ((i >> 20 & 0xFL) / 15.0f - 0.5f) * 0.5f;
        point.z += ((i >> 24 & 0xFL) / 15.0f - 0.5f) * 0.5f;
        return point;
    }

    public static boolean isBlockAirOrPlant(final IBlockState state)
    {
        final Material material = state.getBlock().getMaterial(state);
        return material == Material.AIR || material == Material.PLANTS || material == Material.VINE || NoCubes.isBlockLiquid(block);
    }

    public static boolean doesPointTopIntersectWithAir(final IBlockAccess world, final Vec3d point)
    {
        boolean intersects = false;
        for(int i = 0; i < 4; ++i)
        {
            final int x1 = (int)(point.x - (i & 0x1));
            final int z1 = (int)(point.z - (i >> 1 & 0x1));
            if(!isBlockAirOrPlant(world.getBlockState(new BlockPos(x1, (int)point.y, z1))))
            {
                return false;
            }
            if(isBlockAirOrPlant(world.getBlockState(new BlockPos(x1, (int)point.y - 1, z1))))
            {
                intersects = true;
            }
        }
        return intersects;
    }

    public static boolean doesPointBottomIntersectWithAir(final IBlockAccess world, final Vec3d point)
    {
        boolean intersects = false;
        boolean notOnly = false;
        for(int i = 0; i < 4; ++i)
        {
            final int x1 = (int)(point.x - (i & 0x1));
            final int z1 = (int)(point.z - (i >> 1 & 0x1));
            if(!isBlockAirOrPlant(world.getBlockState(new BlockPos(x1, (int)point.y - 1, z1))))
            {
                return false;
            }
            if(!isBlockAirOrPlant(world.getBlockState(new BlockPos(x1, (int)point.y + 1, z1))))
            {
                notOnly = true;
            }
            if(isBlockAirOrPlant(world.getBlockState(new BlockPos(x1, (int)point.y, z1))))
            {
                intersects = true;
            }
        }
        return intersects && notOnly;
    }

    public static boolean doesPointIntersectWithManufactured(final IBlockAccess world, final Vec3d point)
    {
        for(int i = 0; i < 4; ++i)
        {
            final int x1 = (int)(point.x - (i & 0x1));
            final int z1 = (int)(point.z - (i >> 1 & 0x1));
            final IBlockState state = world.getBlockState(new BlockPos(x1, (int)point.y, z1));
            if(!isBlockAirOrPlant(state) && !NoCubes.isBlockSoft(state.getBlock()))
            {
                return true;
            }
            final IBlockState state2 = world.getBlockState(new BlockPos(x1, (int)point.y - 1, z1));
            if(!isBlockAirOrPlant(state2) && !NoCubes.isBlockSoft(state2.getBlock()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean renderLiquidBlock(final IBlockState block, final int x, final int y, final int z, final RenderBlocks renderer, final IBlockAccess world)
    {
        final boolean rendered = renderer.renderBlockLiquid(block, x, y, z);
        if(NoCubes.isBlockLiquid(getBlock(world,x, y + 1, z)))
        {
            return rendered;
        }
        final int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
        if(NoCubes.isBlockSoft(getBlock(world,x + 1, y, z)))
        {
            this.renderGhostLiquid(block, x + 1, y, z, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z + 1)))
        {
            this.renderGhostLiquid(block, x, y, z + 1, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x - 1, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z - 1)))
        {
            this.renderGhostLiquid(block, x - 1, y, z, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z - 2)) && !NoCubes.isBlockLiquid(getBlock(world,x + 1, y, z - 1)))
        {
            this.renderGhostLiquid(block, x, y, z - 1, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x + 1, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x + 1, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x + 2, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x + 1, y, z + 2)))
        {
            this.renderGhostLiquid(block, x + 1, y, z + 1, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x + 1, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x + 1, y, z - 2)) && !NoCubes.isBlockLiquid(getBlock(world,x + 2, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x + 1, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z - 2)))
        {
            this.renderGhostLiquid(block, x + 1, y, z - 1, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x - 1, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z - 2)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z - 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z - 2)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z)))
        {
            this.renderGhostLiquid(block, x - 1, y, z - 1, brightness, renderer, world);
        }
        if(NoCubes.isBlockSoft(getBlock(world,x - 1, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z + 1)) && !NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z + 2)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z)) && !NoCubes.isBlockLiquid(getBlock(world,x - 2, y, z + 2)) && !NoCubes.isBlockLiquid(getBlock(world,x, y, z + 2)))
        {
            this.renderGhostLiquid(block, x - 1, y, z + 1, brightness, renderer, world);
        }
        return rendered;
    }

    public static Block getBlock(IBlockAccess world, int i, int y, int j) {
		return world.getBlockState(new BlockPos(i, y , j)).getBlock();
	}
	public boolean doesPointIntersectWithLiquid(final int x, final int y, final int z, final IBlockAccess world)
    {
        return NoCubes.isBlockLiquid(getBlock(world,x, y, z)) || NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z)) || NoCubes.isBlockLiquid(getBlock(world,x, y, z - 1)) || NoCubes.isBlockLiquid(getBlock(world,x - 1, y, z - 1)) || NoCubes.isBlockLiquid(getBlock(world,x, y + 1, z)) || NoCubes.isBlockLiquid(getBlock(world,x - 1, y + 1, z)) || NoCubes.isBlockLiquid(getBlock(world,x, y + 1, z - 1)) || NoCubes.isBlockLiquid(getBlock(world,x - 1, y + 1, z - 1));
    }

    public boolean renderGhostLiquid(final IBlockState state, final int x, final int y, final int z, final int brightness, final RenderBlocks renderer, final IBlockAccess world)
    {
        final Tessellator tessellator = Tessellator.instance;
        Block block = state.getBlock();
        final Material material = block.getMaterial(state);
        double height0 = 0.7;
        double height2 = 0.7;
        double height3 = 0.7;
        double height4 = 0.7;
        if(this.doesPointIntersectWithLiquid(x, y, z, world))
        {
            height0 = renderer.getLiquidHeight(x, y, z, material);
        }
        if(this.doesPointIntersectWithLiquid(x, y, z + 1, world))
        {
            height2 = renderer.getLiquidHeight(x, y, z + 1, material);
        }
        if(this.doesPointIntersectWithLiquid(x + 1, y, z + 1, world))
        {
            height3 = renderer.getLiquidHeight(x + 1, y, z + 1, material);
        }
        if(this.doesPointIntersectWithLiquid(x + 1, y, z, world))
        {
            height4 = renderer.getLiquidHeight(x + 1, y, z, material);
        }
        height0 -= 0.0010000000474974513;
        height2 -= 0.0010000000474974513;
        height3 -= 0.0010000000474974513;
        height4 -= 0.0010000000474974513;
        final IIcon icon = renderer.getBlockIconFromSide(block, 1);
        final double minU = icon.getInterpolatedU(0.0);
        final double minV = icon.getInterpolatedV(0.0);
        final double maxU = icon.getInterpolatedU(16.0);
        final double maxV = icon.getInterpolatedV(16.0);
        tessellator.setBrightness(brightness);
        tessellator.setColorOpaque_I(getColor(state,world, new BlockPos(x, y, z)));
        tessellator.addVertexWithUV((double)(x + 0), y + height0, (double)(z + 0), minU, minV);
        tessellator.addVertexWithUV((double)(x + 0), y + height2, (double)(z + 1), minU, maxV);
        tessellator.addVertexWithUV((double)(x + 1), y + height3, (double)(z + 1), maxU, maxV);
        tessellator.addVertexWithUV((double)(x + 1), y + height4, (double)(z + 0), maxU, minV);
        return true;
    }

    public static boolean shouldHookRenderer(final Block block)
    {
        return NoCubes.isNoCubesEnabled && (NoCubes.isBlockSoft(block) || NoCubes.isBlockLiquid(block));
    }

    public boolean directRenderHook(final Block block, final int x, final int y, final int z, final RenderBlocks renderer)
    {
        block.setBlockBoundsBasedOnState(renderer.blockAccess, x, y, z);
        renderer.setRenderBoundsFromBlock(block);
        final IBlockAccess world = renderer.blockAccess;
        if(NoCubes.isBlockLiquid(block))
        {
            return this.renderLiquidBlock(block, x, y, z, renderer, world);
        }
        return this.renderSoftBlock(block, x, y, z, renderer, world);
    }

    public static void inject(final Block block, final World world, final int x, final int y, final int z, final AxisAlignedBB aabb, final List list, final Entity entity)
    {
        final float f = SmoothBlockRenderer2.getSmoothBlockHeightForCollision(world, block, x, y, z);
        final float f2 = SmoothBlockRenderer2.getSmoothBlockHeightForCollision(world, block, x, y, z + 1);
        final float f3 = SmoothBlockRenderer2.getSmoothBlockHeightForCollision(world, block, x + 1, y, z + 1);
        final float f4 = SmoothBlockRenderer2.getSmoothBlockHeightForCollision(world, block, x + 1, y, z);
        addBBoundsToList(x, y, z, 0.0f, 0.0f, 0.0f, 0.5f, f, 0.5f, aabb, list);
        addBBoundsToList(x, y, z, 0.0f, 0.0f, 0.5f, 0.5f, f2, 1.0f, aabb, list);
        addBBoundsToList(x, y, z, 0.5f, 0.0f, 0.5f, 1.0f, f3, 1.0f, aabb, list);
        addBBoundsToList(x, y, z, 0.5f, 0.0f, 0.0f, 1.0f, f4, 0.5f, aabb, list);
    }

    public static void addBBoundsToList(final int x, final int y, final int z, final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ, final AxisAlignedBB aabb, final List list)
    {
        final AxisAlignedBB aabb2 = new AxisAlignedBB(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
        if(aabb2 != null && aabb.intersects(aabb2))
        {
            list.add(aabb2);
        }
    }
}

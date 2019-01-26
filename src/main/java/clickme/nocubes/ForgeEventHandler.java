package clickme.nocubes;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class ForgeEventHandler
{
    private NoCubes noCubes;

    public ForgeEventHandler(final NoCubes mod)
    {
        this.noCubes = mod;
    }

    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event)
    {
        final NoCubes noCubes = this.noCubes;
        if(NoCubes.keyOpenSettings.isPressed())
        {
            this.noCubes.openCubeSettingsGui();
        }
    }
}

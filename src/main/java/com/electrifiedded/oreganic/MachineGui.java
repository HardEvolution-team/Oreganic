package com.electrifiedded.oreganic;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class MachineGui extends GuiContainer {
    private static final ResourceLocation INFUSER = new ResourceLocation("oreganic", "textures/gui/organicinfuser.png");
    private static final ResourceLocation EXTRACTOR = new ResourceLocation("oreganic", "textures/gui/mineralextractor.png");
    private final TileMachine tile;

    public MachineGui(InventoryPlayer pl, TileMachine tile) {
        super(new MachineContainer(pl, tile));
        this.tile = tile;
        xSize = 176;
        ySize = 166;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        super.drawScreen(mx, my, pt);
        renderHoveredToolTip(mx, my);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        String name = I18n.format(tile.getName());
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 4, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
        boolean inf = tile instanceof TileInfuser;
        mc.getTextureManager().bindTexture(inf ? INFUSER : EXTRACTOR);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (tile.energy > 0) {
            int h = inf ? ((TileInfuser) tile).powerScaled(46) : ((TileExtractor) tile).burnScaled(46);
            drawTexturedModalRect(guiLeft + 17, guiTop + 49 - h, 176, 166 - h, 16, h);
        }
        drawTexturedModalRect(guiLeft + 65, guiTop + (inf ? 15 : 34), 176, 0, tile.progressScaled(34), 56);
    }
}

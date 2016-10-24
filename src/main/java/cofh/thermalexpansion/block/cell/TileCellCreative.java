package cofh.thermalexpansion.block.cell;

import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ServerHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.nbt.NBTTagCompound;

public class TileCellCreative extends TileCell {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileCellCreative.class, "thermalexpansion.CellCreative");
	}

	public static final byte[] DEFAULT_SIDES = { 1, 1, 1, 1, 1, 1 };

	public TileCellCreative() {

		energyStorage.setEnergyStored(-1);
	}

	public TileCellCreative(int metadata) {

		super(metadata);
		energyStorage.setEnergyStored(-1);
	}

	@Override
	public byte[] getDefaultSides() {

		return DEFAULT_SIDES.clone();
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!cached) {
			onNeighborBlockChange();
		}
		if (redstoneControlOrDisable()) {
			for (int i = 0; i < 6; i++) {
				transferEnergy(i);
			}
		}
	}

	@Override
	protected void transferEnergy(int bSide) {

		if (sideCache[bSide] != 1) {
			return;
		}
		if (adjacentHandlers[bSide] == null) {
			return;
		}
		adjacentHandlers[bSide].receiveEnergy(EnumFacing.VALUES[bSide ^ 1], energySend, false);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		if (from == null || sideCache[from.ordinal()] == 2) {
			return Math.min(maxReceive, energyReceive);
		}
		return 0;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		if (from == null || sideCache[from.ordinal()] == 1) {
			return Math.min(maxExtract, energySend);
		}
		return 0;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			return IconRegistry.getIcon("FluidRedstone");
		} else if (pass == 1) {
			return IconRegistry.getIcon("Cell", type * 2);
		} else if (pass == 2) {
			return IconRegistry.getIcon(BlockCell.textureSelection, sideCache[side]);
		}
		return side != facing ? IconRegistry.getIcon(BlockCell.textureSelection, 0) : IconRegistry.getIcon("CellMeterCreative");
	}

}

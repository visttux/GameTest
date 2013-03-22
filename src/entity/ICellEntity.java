package entity;

/** Esta interfaz permitira trabajar con celdas */
public interface ICellEntity {

	public abstract int getCellX();
	public abstract int getCellY();

	public abstract void setCell(final ICellEntity pCellEntity);
	public abstract void setCell(final int pCellX, final int pCellY);

	public abstract boolean isInSameCell(final ICellEntity pCellEntity);
}

package de.failender.dsaonline.kampf.updates;

public class UpdateGegnerPosition {

	private final int gegner;
	private final int x;
	private final int y;

	public UpdateGegnerPosition(int gegner, int x, int y) {
		this.gegner = gegner;
		this.x = x;
		this.y = y;
	}

	public int getGegner() {
		return gegner;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

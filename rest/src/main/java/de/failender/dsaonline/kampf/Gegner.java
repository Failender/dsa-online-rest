package de.failender.dsaonline.kampf;

public class Gegner {

	public static final String ICON_AXE = "axe";
	public static final String ICON_BOW = "bow";
	public static final String ICON_MAGE = "mage";
	public static final String ICON_DUALSWORD = "dualsword";

	private int id;
	private String icon;
	private int x;
	private int y;

	private int hp;
	private int maxHp;
	private boolean ally;

	public Gegner() {
	}

	public Gegner(String icon, int x, int y, int hp, int maxHp, boolean ally) {
		this.icon = icon;
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.maxHp = maxHp;
		this.ally = ally;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public String getIcon() {
		return icon;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAlly() {
		return ally;
	}

	// hp percentage of the enemy. goes in steps of 0.25
	public float getHpPercentage() {
		return ((hp * 4) / maxHp) * 0.25f;
	}
}

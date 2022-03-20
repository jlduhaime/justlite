package net.runelite.client.plugins.barbarianassault;

public enum DeathTimesMode
{
    BOTH("Both"),
    CHAT_BOX("Chat Box"),
    INFO_BOX("Info Box");

    private final String name;

    DeathTimesMode(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
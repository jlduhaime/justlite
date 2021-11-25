package net.runelite.client.plugins.resourcepacks.event;

import java.util.List;
import lombok.Value;
import net.runelite.client.plugins.resourcepacks.hub.ResourcePackManifest;

@Value
public class ResourcePacksChanged
{
	List<ResourcePackManifest> newManifest;
}

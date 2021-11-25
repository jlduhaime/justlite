package net.runelite.client.plugins.inventorysetups;

import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.vars.InputType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InventorySetupsBankSearch
{
    private final Client client;
    private final ClientThread clientThread;

    @Inject
    private InventorySetupsBankSearch(
            final Client client,
            final ClientThread clientThread
    )
    {
        this.client = client;
        this.clientThread = clientThread;
    }

    public void layoutBank()
    {
        Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankContainer == null || bankContainer.isHidden())
        {
            return;
        }

        Object[] scriptArgs = bankContainer.getOnInvTransmitListener();
        if (scriptArgs == null)
        {
            return;
        }

        client.runScript(scriptArgs);
    }

    public void initSearch()
    {
        clientThread.invoke(() ->
        {
            Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
            if (bankContainer == null || bankContainer.isHidden())
            {
                return;
            }

            Object[] bankBuildArgs = bankContainer.getOnInvTransmitListener();
            if (bankBuildArgs == null)
            {
                return;
            }

            // the search toggle script requires 1 as its first argument
            Object[] searchToggleArgs = ArrayUtils.insert(1, bankBuildArgs, 1);
            searchToggleArgs[0] = ScriptID.BANKMAIN_SEARCH_TOGGLE;

            // reset search to clear tab tags and also allow us to initiate a new search while searching
            reset(true);
            client.runScript(searchToggleArgs);
        });
    }

    public void reset(boolean closeChat)
    {
        clientThread.invoke(() ->
        {
            // This ensures that any chatbox input (e.g from search) will not remain visible when
            // selecting/changing tab
            if (closeChat)
            {
                // this clears the input text and type, and resets the chatbox to allow input
                client.runScript(ScriptID.MESSAGE_LAYER_CLOSE, 1, 1, 0);
            }
            else
            {
                client.setVar(VarClientInt.INPUT_TYPE, InputType.NONE.getType());
                client.setVar(VarClientStr.INPUT_TEXT, "");
            }

            layoutBank();
        });
    }
}

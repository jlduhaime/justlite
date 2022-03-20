package net.runelite.client.plugins.barbarianassault;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatMessageBuilder;


@Getter(AccessLevel.PACKAGE)
public class Scorecard
{
    private BarbarianAssaultPlugin game;

    @Getter(AccessLevel.NONE)
    private List<Wave> waves = new ArrayList<>();
    private String[] totalDescriptions = {
            "A: ",
            "; D: ",
            "; C: ",
            "; Vial: ",
            "; H packs: ",
            "; Total: "};
    private String[] otherPointsDescriptions = {
            " A: ",
            "; D: ",
            "; C: ",
            "; H: "
    };
    private int[] totalPoints = new int[6];
    private int[] totalAmounts = new int[6];
    private int[] otherRolesPoints = new int[4];

    Scorecard(BarbarianAssaultPlugin game)
    {
        this.game = game;
    }

    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getMessage().startsWith("---- Points:") && game.getStage() == 1)
        {
            totalPoints = new int[6];
            totalAmounts = new int[6];
        }
    }

    void addWave(Wave wave)
    {
        this.waves.add(wave);
    }

    int getNumberOfWaves()
    {
        return waves.size();
    }

    ChatMessageBuilder getGameSummary()
    {
        int[] amountsList;
        int[] pointsList;
        int[] otherRolesPointsList;
        ChatMessageBuilder message = new ChatMessageBuilder();
        message.append("Game points: ");
        for (Wave wave : waves)
        {
            amountsList = wave.getAmounts();
            pointsList = wave.getPoints();
            otherRolesPointsList = wave.getOtherRolesPointsList();
            for (int j = 0; j < totalAmounts.length; j++)
            {
                totalAmounts[j] += amountsList[j];
            }
            for (int k = 0; k < totalPoints.length; k++)
            {
                totalPoints[k] += pointsList[k];
            }
            for (int z = 0; z < otherRolesPoints.length; z++)
            {
                otherRolesPoints[z] += otherRolesPointsList[z];
            }
        }
        for (int i = 0; i < otherRolesPoints.length; i++)
        {
            otherRolesPoints[i] += 80;
        }
        totalAmounts[5] += 80;
        for (int i = 0; i < totalDescriptions.length; i++)
        {
            if (i != 4)
            {
                message.append(totalDescriptions[i]);
                message.append(String.valueOf(totalAmounts[i]));
                message.append("(");
                if (totalPoints[i] < 0)
                {
                    message.append(Color.RED, String.valueOf(totalPoints[i]));
                }
                else if (totalPoints[i] > 0)
                {
                    message.append(Color.BLUE, String.valueOf(totalPoints[i]));
                }
                else
                {
                    message.append(String.valueOf(totalPoints[i]));
                }
                message.append(")");
            }
        }
        message.append(System.getProperty("line.separator"));
        message.append("All roles points this game: ");
        for (int i = 0; i < otherPointsDescriptions.length; i++)
        {
            message.append(otherPointsDescriptions[i]);
            message.append(String.valueOf(otherRolesPoints[i]));
        }
        return message;
    }
}
package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sergei on 3/5/17.
 */
public class BotClient extends Client {


    public class BotSocketThread extends SocketThread {

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);

            if (message.contains(":")) {
                String userName = message.split(": ")[0];
                String clientMessageText = message.split(": ")[1];

                SimpleDateFormat dateFormat = null;
                if (clientMessageText.equals("дата")) {
                    dateFormat = new SimpleDateFormat("d.MM.YYYY");
                } else if (clientMessageText.equals("день")) {
                    dateFormat = new SimpleDateFormat("d");
                } else if (clientMessageText.equals("месяц")) {
                    dateFormat = new SimpleDateFormat("MMMM");
                } else if (clientMessageText.equals("год")) {
                    dateFormat = new SimpleDateFormat("YYYY");
                } else if (clientMessageText.equals("время")) {
                    dateFormat = new SimpleDateFormat("H:mm:ss");
                } else if (clientMessageText.equals("час")) {
                    dateFormat = new SimpleDateFormat("H");
                } else if (clientMessageText.equals("минуты")) {
                    dateFormat = new SimpleDateFormat("m");
                } else if (clientMessageText.equals("секунды")) {
                    dateFormat = new SimpleDateFormat("s");
                }
                if (dateFormat != null) {
                    Date date = Calendar.getInstance().getTime();
                    String dateString = dateFormat.format(date);
                    String messageToSend =
                            String.format("Информация для %s: %s", userName, dateString);
                    sendTextMessage(messageToSend);
                }
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        int randomNumber = (int) (Math.random() * 100);
        return "date_bot_" + randomNumber;
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}

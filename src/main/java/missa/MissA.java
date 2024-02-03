package missa;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.scene.Scene;
import missa.command.Command;
import missa.exception.IncorrectTaskTypeException;
import missa.exception.NoContentException;
import missa.exception.NoSuchTaskException;
import missa.exception.NoTimingException;
import missa.exception.WrongTaskDataException;

/**
 * A chatbot class named as MissA.
 * Records 3 types of tasks for users.
 */
public class MissA {
    private TaskList tasks;
    private Ui ui = new Ui();
    private Storage storage;
    private Parser parser = new Parser();
    private boolean isBye = false;
    private Scene scene;

    /**
     * Create a MissA object.
     *
     * @param filePath Data file to be tracked.
     */
    public MissA(String filePath) {
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.loadData());
        } catch (WrongTaskDataException | FileNotFoundException e) {
            System.out.println(ui.showError(e));
            tasks = new TaskList();
        }
    }

    /**
     * Executes commands and replies to users.
     *
     * @param input User input to be analysed.
     * @return A string of messages indicating the task is done.
     */
    public String getResponse(String input) {
        try {
            Command command = parser.parse(input, tasks);
            tasks = command.execute();
            String newData = tasks.getUpdatedData();
            storage.writeBackData(newData);
            return command.getReply(ui);
        } catch (IncorrectTaskTypeException
                 | NoSuchTaskException
                 | NoTimingException
                 | NoContentException e) {
            return ui.showError(e);
        } catch (IOException e) {
            return "Sorry, I am unable to update data file.";
        }
    }

    /**
     * Checks if this is a bye command.
     *
     * @param input User input to be analysed.
     * @return True if this is a bye command.
     */
    public boolean checkBye(String input) {
        if (input.equals("bye")) {
            return true;
        }
        return false;
    }

    /**
     * Returns welcome message to user.
     *
     * @return A string of welcome message.
     */
    public String getWelcomeMsg() {
        return ui.sayHi();
    }

}

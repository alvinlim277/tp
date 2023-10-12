package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.logic.commands.EditCommand.EditSpecialistDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Patient;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Specialist;
import seedu.address.testutil.EditPatientDescriptorBuilder;
import seedu.address.testutil.EditSpecialistDescriptorBuilder;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.PatientUtil;
import seedu.address.testutil.SpecialistBuilder;
import seedu.address.testutil.SpecialistUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add_patient() throws Exception {
        Patient patient = new PatientBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PatientUtil.getAddCommand(patient));
        assertEquals(new AddCommand(patient), command);
    }

    @Test
    public void parseCommand_add_specialist() throws Exception {
        Specialist specialist = new SpecialistBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(SpecialistUtil.getAddCommand(specialist));
        assertEquals(new AddCommand(specialist), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete_patient() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + CliSyntax.SPECIALIST_TAG
                        + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }
    @Test
    public void parseCommand_delete_specialist() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + CliSyntax.SPECIALIST_TAG
                        + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit_patient() throws Exception {
        Patient specialist = new PatientBuilder().build();
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder(specialist).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD
                + " " + CliSyntax.PATIENT_TAG + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PatientUtil.getEditPatientDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor, PersonType.PATIENT), command);
    }

    @Test
    public void parseCommand_edit_specialist() throws Exception {
        Specialist person = new SpecialistBuilder().build();
        EditSpecialistDescriptor descriptor = new EditSpecialistDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD
                + " " + CliSyntax.SPECIALIST_TAG + " "
                + INDEX_FIRST_PERSON.getOneBased()
                + " " + SpecialistUtil.getEditSpecialistDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor, PersonType.SPECIALIST), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find_patient() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + CliSyntax.PATIENT_TAG + " "
                        + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords), PersonType.PATIENT), command);
    }

    @Test
    public void parseCommand_find_specialist() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + CliSyntax.SPECIALIST_TAG + " "
                        + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords), PersonType.SPECIALIST), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(
                ListCommand.COMMAND_WORD + " " + CliSyntax.PATIENT_TAG) instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}

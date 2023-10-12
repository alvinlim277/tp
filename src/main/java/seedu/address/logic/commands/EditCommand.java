package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PATIENT_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SPECIALTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.SPECIALIST_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonType;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Specialist;
import seedu.address.model.person.Specialty;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Specify whether the person is a patient or specialist using the "
            + PATIENT_TAG + " or " + SPECIALIST_TAG + " tags. "
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "If the person is a specialist, edit their specialty by using the "
            + PREFIX_SPECIALTY + " prefix. \n"
            + "Example: " + COMMAND_WORD + " "
            + PATIENT_TAG + " "
            + "1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    private final PersonType personType;

    /**
     * @param index                of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     * @param personType The type of person being edited i.e. patient or specialist
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor, PersonType personType) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        if (editPersonDescriptor instanceof EditPatientDescriptor) {
            this.editPersonDescriptor = new EditPatientDescriptor((EditPatientDescriptor) editPersonDescriptor);
        } else {
            this.editPersonDescriptor = new EditSpecialistDescriptor((EditSpecialistDescriptor) editPersonDescriptor);
        }
        this.personType = personType;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson;
        if (personToEdit instanceof Patient) {
            if (!(editPersonDescriptor instanceof EditPatientDescriptor)) {
                throw new CommandException(Messages.MESSAGE_PERSON_TYPE_MISMATCH_INDEX);
            }
            editedPerson = createEditedPatient((Patient) personToEdit, (EditPatientDescriptor) editPersonDescriptor);
        } else {
            if (!(editPersonDescriptor instanceof EditSpecialistDescriptor)) {
                throw new CommandException(Messages.MESSAGE_PERSON_TYPE_MISMATCH_INDEX);
            }
            editedPerson = createEditedSpecialist((Specialist) personToEdit,
                    (EditSpecialistDescriptor) editPersonDescriptor);
        }

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(personType.getSearchPredicate());
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Patient} with the details of {@code patientToEdit}
     * edited with {@code editPatientDescriptor}.
     */
    private static Patient createEditedPatient(Patient patientToEdit, EditPatientDescriptor editPatientDescriptor) {
        assert patientToEdit != null;

        Name updatedName = editPatientDescriptor.getName().orElse(patientToEdit.getName());
        Phone updatedPhone = editPatientDescriptor.getPhone().orElse(patientToEdit.getPhone());
        Email updatedEmail = editPatientDescriptor.getEmail().orElse(patientToEdit.getEmail());
        Address updatedAddress = editPatientDescriptor.getAddress().orElse(patientToEdit.getAddress());
        Set<Tag> updatedTags = editPatientDescriptor.getTags().orElse(patientToEdit.getTags());

        return new Patient(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags);
    }

    /**
     * Creates and returns a {@code Specialist} with the details of {@code specialistToEdit}
     * edited with {@code editSpecialistDescriptor}.
     */
    private static Specialist createEditedSpecialist(Specialist specialistToEdit,
                                                     EditSpecialistDescriptor editSpecialistDescriptor) {
        assert specialistToEdit != null;

        Name updatedName = editSpecialistDescriptor.getName().orElse(specialistToEdit.getName());
        Phone updatedPhone = editSpecialistDescriptor.getPhone().orElse(specialistToEdit.getPhone());
        Email updatedEmail = editSpecialistDescriptor.getEmail().orElse(specialistToEdit.getEmail());
        Address updatedAddress = editSpecialistDescriptor.getAddress().orElse(specialistToEdit.getAddress());
        Set<Tag> updatedTags = editSpecialistDescriptor.getTags().orElse(specialistToEdit.getTags());
        Specialty updatedSpecialty = editSpecialistDescriptor.getSpecialty().orElse(specialistToEdit.getSpecialty());


        return new Specialist(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags, updatedSpecialty);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public abstract static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }

    /**
     * Stores the details to edit the patient with. Each non-empty field value will replace the
     * corresponding field value of the patient.
     */
    public static class EditPatientDescriptor extends EditPersonDescriptor {
        public EditPatientDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPatientDescriptor(EditPatientDescriptor toCopy) {
            super(toCopy);
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other);
        }
        @Override
        public String toString() {
            return super.toString();
        }
    }

    /**
     * Stores the details to edit the specialist with. Each non-empty field value will replace the
     * corresponding field value of the specialist.
     */
    public static class EditSpecialistDescriptor extends EditPersonDescriptor {
        private Specialty specialty;
        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditSpecialistDescriptor(EditSpecialistDescriptor toCopy) {
            super(toCopy);
            setSpecialty(toCopy.specialty);
        }
        public EditSpecialistDescriptor() {}
        public void setSpecialty(Specialty specialty) {
            this.specialty = specialty;
        }

        public Optional<Specialty> getSpecialty() {
            return Optional.ofNullable(specialty);
        }

        @Override
        public boolean equals(Object other) {
            if (super.equals(other) && other instanceof EditSpecialistDescriptor) {
                EditSpecialistDescriptor otherEditSpecialistDescriptor = (EditSpecialistDescriptor) other;
                return Objects.equals(specialty, otherEditSpecialistDescriptor.specialty);
            }
            return false;
        }

        @Override
        public String toString() {
            String stringToAdd = ", specialty=" + specialty;
            return StringUtil.addFieldToPersonToString(stringToAdd, super.toString());
        }

        /**
         * Returns true if at least one field is edited.
         */
        @Override
        public boolean isAnyFieldEdited() {
            return super.isAnyFieldEdited() || CollectionUtil.isAnyNonNull(specialty);
        }
    }
}

package seedu.address.model.person.predicates;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Tests that a {@code Person}'s {@code Tags} matches any of the keywords given.
 */
public class TagsContainsKeywordsPredicate implements Predicate<Person> {

    private final List<String> keywords;

    public TagsContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return person.getTags()
                .stream()
                .map(tag -> keywords
                        .stream()
                        .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(tag.tagName, keyword)))
                .reduce(false, (x, y) -> x || y);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagsContainsKeywordsPredicate)) {
            return false;
        }

        TagsContainsKeywordsPredicate otherTagsContainsKeywordsPredicate = (TagsContainsKeywordsPredicate) other;
        return keywords.equals(otherTagsContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}

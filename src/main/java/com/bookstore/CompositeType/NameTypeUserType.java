package com.bookstore.CompositeType;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NameTypeUserType implements UserType<PersonName> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<PersonName> returnedClass() {
        return PersonName.class;
    }

    @Override
    public PersonName nullSafeGet(
            ResultSet rs,
            int position,
            WrapperOptions options
    ) throws SQLException {
        Object value = rs.getObject(position);

        if (value == null) {
            return null;
        }

        return parsePersonName(value.toString());
    }

    @Override
    public void nullSafeSet(
            PreparedStatement st,
            PersonName value,
            int index,
            WrapperOptions options
    ) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }

        st.setObject(index, toCompositeText(value), Types.OTHER);
    }

    @Override
    public boolean equals(PersonName x, PersonName y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(PersonName x) {
        return Objects.hashCode(x);
    }

    @Override
    public PersonName deepCopy(PersonName value) {
        if (value == null) {
            return null;
        }

        return new PersonName(value.firstName(), value.lastName());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PersonName value) {
        return value;
    }

    @Override
    public PersonName assemble(Serializable cached, Object owner) {
        return (PersonName) cached;
    }

    @Override
    public PersonName replace(
            PersonName detached,
            PersonName managed,
            Object owner
    ) {
        return deepCopy(detached);
    }

    private static String toCompositeText(PersonName personName) {
        return "("
                + quoteCompositeField(personName.firstName())
                + ","
                + quoteCompositeField(personName.lastName())
                + ")";
    }

    private static String quoteCompositeField(String value) {
        if (value == null) {
            return "";
        }

        return "\""
                + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                + "\"";
    }

    private static PersonName parsePersonName(String compositeText) {
        List<String> fields = parseCompositeFields(compositeText);

        if (fields.size() != 2) {
            throw new IllegalArgumentException("Invalid name_type composite value: " + compositeText);
        }

        return new PersonName(fields.get(0), fields.get(1));
    }

    private static List<String> parseCompositeFields(String compositeText) {
        String text = compositeText.trim();

        if (text.startsWith("(") && text.endsWith(")")) {
            text = text.substring(1, text.length() - 1);
        }

        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inQuotes = false;
        boolean wasQuoted = false;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (inQuotes) {
                if (ch == '\\' && i + 1 < text.length()) {
                    current.append(text.charAt(i + 1));
                    i++;
                } else if (ch == '"') {
                    inQuotes = false;
                } else {
                    current.append(ch);
                }
            } else {
                if (ch == '"') {
                    inQuotes = true;
                    wasQuoted = true;
                } else if (ch == ',') {
                    fields.add(toNullableField(current.toString(), wasQuoted));
                    current.setLength(0);
                    wasQuoted = false;
                } else {
                    current.append(ch);
                }
            }
        }

        fields.add(toNullableField(current.toString(), wasQuoted));

        return fields;
    }

    private static String toNullableField(String value, boolean wasQuoted) {
        if (!wasQuoted && value.isEmpty()) {
            return null;
        }

        return value;
    }
}
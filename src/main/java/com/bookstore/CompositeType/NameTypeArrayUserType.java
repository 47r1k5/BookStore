package com.bookstore.CompositeType;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NameTypeArrayUserType implements UserType<List<PersonName>> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<PersonName>> returnedClass() {
        return (Class<List<PersonName>>) (Class<?>) List.class;
    }

    /**
     * Hibernate 7-compatible extractor.
     * Do NOT use the old SharedSessionContractImplementor version.
     */
    @Override
    public List<PersonName> nullSafeGet(
            ResultSet rs,
            int position,
            WrapperOptions options
    ) throws SQLException {
        Array sqlArray = rs.getArray(position);

        if (sqlArray == null) {
            return null;
        }

        Object rawArray = sqlArray.getArray();

        if (!(rawArray instanceof Object[] values)) {
            throw new SQLException("Expected Object[] for name_type[], got: " + rawArray.getClass());
        }

        List<PersonName> result = new ArrayList<>();

        for (Object value : values) {
            if (value == null) {
                result.add(null);
                continue;
            }

            String compositeText = value.toString();
            result.add(parsePersonName(compositeText));
        }

        return result;
    }

    /**
     * Hibernate 7-compatible binder.
     */
    @Override
    public void nullSafeSet(
            PreparedStatement st,
            List<PersonName> value,
            int index,
            WrapperOptions options
    ) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }

        String[] compositeValues = new String[value.size()];

        for (int i = 0; i < value.size(); i++) {
            PersonName personName = value.get(i);

            if (personName == null) {
                compositeValues[i] = null;
            } else {
                compositeValues[i] = toCompositeText(personName);
            }
        }

        Array array = st.getConnection().createArrayOf("name_type", compositeValues);
        st.setArray(index, array);
    }

    @Override
    public boolean equals(List<PersonName> x, List<PersonName> y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(List<PersonName> x) {
        return Objects.hashCode(x);
    }

    @Override
    public List<PersonName> deepCopy(List<PersonName> value) {
        if (value == null) {
            return null;
        }

        return new ArrayList<>(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(List<PersonName> value) {
        if (value == null) {
            return null;
        }

        return new ArrayList<>(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonName> assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }

        return new ArrayList<>((List<PersonName>) cached);
    }

    @Override
    public List<PersonName> replace(
            List<PersonName> detached,
            List<PersonName> managed,
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
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

public class AddressUserType implements UserType<Address> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Address> returnedClass() {
        return Address.class;
    }

    @Override
    public Address nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        Object value = rs.getObject(position);
        if (value == null) {
            return null;
        }
        return parseAddress(value.toString());
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Address value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        st.setObject(index, toCompositeText(value), Types.OTHER);
    }

    @Override
    public boolean equals(Address x, Address y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Address x) {
        return Objects.hashCode(x);
    }

    @Override
    public Address deepCopy(Address value) {
        if (value == null) {
            return null;
        }
        return new Address(value.street(), value.city(), value.num(), value.postalCode(), value.country());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Address value) {
        return value;
    }

    @Override
    public Address assemble(Serializable cached, Object owner) {
        return (Address) cached;
    }

    @Override
    public Address replace(Address detached, Address managed, Object owner) {
        return deepCopy(detached);
    }

    private static String toCompositeText(Address address) {
        return "("
                + quoteCompositeField(address.street()) + ","
                + quoteCompositeField(address.city()) + ","
                + quoteCompositeField(address.num()) + ","
                + quoteCompositeField(address.postalCode()) + ","
                + quoteCompositeField(address.country())
                + ")";
    }

    private static Address parseAddress(String compositeText) {
        List<String> fields = parseCompositeFields(compositeText);
        if (fields.size() != 5) {
            throw new IllegalArgumentException("Invalid address_type composite value: " + compositeText);
        }
        return new Address(fields.get(0), fields.get(1), fields.get(2), fields.get(3), fields.get(4));
    }

    private static String quoteCompositeField(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
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

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

public class Size2DUserType implements UserType<Size2D> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Size2D> returnedClass() {
        return Size2D.class;
    }

    @Override
    public Size2D nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        Object value = rs.getObject(position);
        if (value == null) {
            return null;
        }
        return parseSize2D(value.toString());
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Size2D value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        st.setObject(index, toCompositeText(value), Types.OTHER);
    }

    @Override
    public boolean equals(Size2D x, Size2D y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Size2D x) {
        return Objects.hashCode(x);
    }

    @Override
    public Size2D deepCopy(Size2D value) {
        if (value == null) {
            return null;
        }
        return new Size2D(value.x(), value.y());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Size2D value) {
        return value;
    }

    @Override
    public Size2D assemble(Serializable cached, Object owner) {
        return (Size2D) cached;
    }

    @Override
    public Size2D replace(Size2D detached, Size2D managed, Object owner) {
        return deepCopy(detached);
    }

    private static String toCompositeText(Size2D size) {
        return "("
                + nullableNumber(size.x()) + ","
                + nullableNumber(size.y())
                + ")";
    }

    private static String nullableNumber(Number value) {
        return value == null ? "" : value.toString();
    }

    private static Size2D parseSize2D(String compositeText) {
        List<String> fields = parseCompositeFields(compositeText);
        if (fields.size() != 2) {
            throw new IllegalArgumentException("Invalid size_2d_type composite value: " + compositeText);
        }
        Short x = fields.get(0) == null ? null : Short.valueOf(fields.get(0));
        Short y = fields.get(1) == null ? null : Short.valueOf(fields.get(1));
        return new Size2D(x, y);
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

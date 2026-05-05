package com.bookstore.CompositeType;

import com.bookstore.Enum.ProductType;
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

public class CartArrayUserType implements UserType<List<PurchaseCartItem>> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<List<PurchaseCartItem>> returnedClass() {
        return (Class<List<PurchaseCartItem>>) (Class<?>) List.class;
    }

    @Override
    public List<PurchaseCartItem> nullSafeGet(
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
            throw new SQLException("Expected Object[] for cart[], got: " + rawArray.getClass());
        }

        List<PurchaseCartItem> result = new ArrayList<>();

        for (Object value : values) {
            if (value == null) {
                result.add(null);
                continue;
            }

            result.add(parsePurchaseCartItem(value.toString()));
        }

        return result;
    }

    @Override
    public void nullSafeSet(
            PreparedStatement st,
            List<PurchaseCartItem> value,
            int index,
            WrapperOptions options
    ) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }

        String[] compositeValues = new String[value.size()];

        for (int i = 0; i < value.size(); i++) {
            PurchaseCartItem item = value.get(i);

            if (item == null) {
                compositeValues[i] = null;
            } else {
                compositeValues[i] = toCompositeText(item);
            }
        }

        Array array = st.getConnection().createArrayOf("cart", compositeValues);
        st.setArray(index, array);
    }

    @Override
    public boolean equals(List<PurchaseCartItem> x, List<PurchaseCartItem> y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(List<PurchaseCartItem> x) {
        return Objects.hashCode(x);
    }

    @Override
    public List<PurchaseCartItem> deepCopy(List<PurchaseCartItem> value) {
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
    public Serializable disassemble(List<PurchaseCartItem> value) {
        if (value == null) {
            return null;
        }

        return new ArrayList<>(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PurchaseCartItem> assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }

        return new ArrayList<>((List<PurchaseCartItem>) cached);
    }

    @Override
    public List<PurchaseCartItem> replace(
            List<PurchaseCartItem> detached,
            List<PurchaseCartItem> managed,
            Object owner
    ) {
        return deepCopy(detached);
    }

    private static String toCompositeText(PurchaseCartItem item) {
        return "("
                + item.cartId()
                + ","
                + quoteCompositeField(item.productId())
                + ","
                + quoteCompositeField(item.prodType().name())
                + ","
                + item.quantity()
                + ")";
    }

    private static PurchaseCartItem parsePurchaseCartItem(String compositeText) {
        List<String> fields = parseCompositeFields(compositeText);

        if (fields.size() != 4) {
            throw new IllegalArgumentException("Invalid cart composite value: " + compositeText);
        }

        Integer cartId = fields.get(0) == null ? null : Integer.valueOf(fields.get(0));
        String productId = fields.get(1);
        ProductType prodType = fields.get(2) == null ? null : ProductType.valueOf(fields.get(2).toUpperCase());
        Short quantity = fields.get(3) == null ? null : Short.valueOf(fields.get(3));

        return new PurchaseCartItem(
                cartId,
                productId,
                prodType,
                quantity
        );
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
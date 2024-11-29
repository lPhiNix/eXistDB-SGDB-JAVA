package org.phinix.lib.common.util;

/**
 * XQueryFactory is a class responsible for building XQuery queries.
 * It does not execute the queries nor map the results,
 * it only generates the query string based on the provided filters.
 */
public class XQueryFactory {
    /**
     * Method to build an XQuery query with filters.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param filters        The filters to be applied in the XQuery.
     * @return               The constructed XQuery.
     */
    public static String buildQuery(String collectionPath, Class<?> clazz, String... filters) throws XMLSerializableNotFoundException {
        if (!XMLFileUtil.isXMLSerializable(clazz)) {
            throw new XMLSerializableNotFoundException();
        }

        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("')//").append(XMLFileUtil.getObjectTagName(clazz)).append(" ");

        // Add the filters to the WHERE clause
        if (filters != null && filters.length > 0) {
            query.append("where ");
            appendFilters(query, filters);
        }

        // Return the full object (no specific fields selected)
        query.append(" return $item");

        return query.toString();
    }

    /**
     * Method to build an XQuery query with filters and grouping.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param groupByField   The field to group by.
     * @param filters        The filters to be applied in the XQuery.
     * @return               The constructed XQuery.
     */
    public static String buildQueryWithGroupBy(String collectionPath, Class<?> clazz, String groupByField, String... filters) throws XMLSerializableNotFoundException {
        if (!XMLFileUtil.isXMLSerializable(clazz)) {
            throw new XMLSerializableNotFoundException();
        }

        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("')//").append(XMLFileUtil.getObjectTagName(clazz)).append(" ");

        // Add the filters to the WHERE clause
        if (filters != null && filters.length > 0) {
            query.append("where ");
            appendFilters(query, filters);
        }

        // Group by a specific field
        if (groupByField != null && !groupByField.isEmpty()) {
            query.append(" group by $item/").append(groupByField).append(" ");
        }

        // Return the full object (no specific fields selected)
        query.append("return $item");

        return query.toString();
    }

    /**
     * Helper method to append filters to the query.
     *
     * @param query   The StringBuilder to append the filters.
     * @param filters The array of filters to process.
     */
    private static void appendFilters(StringBuilder query, String... filters) {
        for (int i = 0; i < filters.length; i++) {
            String[] parts = filters[i].split(" ", 3); // Expecting "field operator value"
            if (parts.length != 3) continue;

            query.append("$item/").append(parts[0]).append(" ").append(parts[1]).append(" ").append(parts[2]);

            if (i < filters.length - 1) {
                query.append(" and ");
            }
        }
    }
}

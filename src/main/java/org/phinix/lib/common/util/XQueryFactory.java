package org.phinix.lib.common.util;

/**
 * XQueryFactory is a class responsible for building XQuery queries.
 * It does not execute the queries nor map the results,
 * it only generates the query string based on the provided filters.
 */
public class XQueryFactory {

    /**
     * The unique method to build a basic XQuery query.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param filters        The filters to be applied in the XQuery.
     * @return The constructed XQuery.
     */
    public static String buildQuery(String collectionPath, String... filters) {
        return buildQuery(collectionPath, null, null, filters);
    }

    /**
     * Method to build an XQuery query with selected fields and filters.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param fields         The specific fields to retrieve.
     * @param filters        The filters to be applied in the XQuery.
     * @return The constructed XQuery.
     */
    public static String buildQuery(String collectionPath, String[] fields, String... filters) {
        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("') ");

        // Add the filters to the WHERE clause
        if (filters != null && filters.length > 0) {
            query.append("where ");
            for (int i = 0; i < filters.length; i++) {
                String[] parts = filters[i].split("=");
                if (parts.length != 2) continue;

                query.append("$item/").append(parts[0]).append(" = '").append(parts[1]).append("'");

                if (i < filters.length - 1) {
                    query.append(" and ");
                }
            }
        }

        // If fields are specified, select them specifically
        if (fields != null && fields.length > 0) {
            query.append("return ");
            query.append("<result>");
            for (String field : fields) {
                query.append("<").append(field).append(">{$item/").append(field).append("}</").append(field).append(">");
            }
            query.append("</result>");
        } else {
            query.append("return $item");
        }

        return query.toString();
    }

    /**
     * Method to build an XQuery query with complex filters and grouping.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param fields         The specific fields to retrieve.
     * @param groupByField   The field to group by.
     * @param filters        The filters to be applied in the XQuery.
     * @return The constructed XQuery.
     */
    public static String buildQuery(String collectionPath, String[] fields, String groupByField, String... filters) {
        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("') ");

        // Add the filters to the WHERE clause
        if (filters != null && filters.length > 0) {
            query.append("where ");
            for (int i = 0; i < filters.length; i++) {
                String[] parts = filters[i].split("=");
                if (parts.length != 2) continue;

                query.append("$item/").append(parts[0]).append(" = '").append(parts[1]).append("'");

                if (i < filters.length - 1) {
                    query.append(" and ");
                }
            }
        }

        // Group by a specific field
        if (groupByField != null && !groupByField.isEmpty()) {
            query.append(" group by $item/").append(groupByField);
        }

        // If fields are specified, select them specifically
        if (fields != null && fields.length > 0) {
            query.append("return ");
            query.append("<result>");
            for (String field : fields) {
                query.append("<").append(field).append(">{$item/").append(field).append("}</").append(field).append(">");
            }
            query.append("</result>");
        } else {
            query.append("return $item");
        }

        return query.toString();
    }

    /**
     * Method to build an XQuery query with filters, grouping, and sorting.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param fields         The specific fields to retrieve.
     * @param groupByField   The field to group by.
     * @param orderByField   The field to sort by.
     * @param filters        The filters to be applied in the XQuery.
     * @return The constructed XQuery.
     */
    public static String buildQuery(String collectionPath, String[] fields, String groupByField, String orderByField, String... filters) {
        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("') ");

        // Add the filters to the WHERE clause
        if (filters != null && filters.length > 0) {
            query.append("where ");
            for (int i = 0; i < filters.length; i++) {
                String[] parts = filters[i].split("=");
                if (parts.length != 2) continue;

                query.append("$item/").append(parts[0]).append(" = '").append(parts[1]).append("'");

                if (i < filters.length - 1) {
                    query.append(" and ");
                }
            }
        }

        // Group by a specific field
        if (groupByField != null && !groupByField.isEmpty()) {
            query.append(" group by $item/").append(groupByField);
        }

        // Sort by a specific field
        if (orderByField != null && !orderByField.isEmpty()) {
            query.append(" order by $item/").append(orderByField);
        }

        // If fields are specified, select them specifically
        if (fields != null && fields.length > 0) {
            query.append("return ");
            query.append("<result>");
            for (String field : fields) {
                query.append("<").append(field).append(">{$item/").append(field).append("}</").append(field).append(">");
            }
            query.append("</result>");
        } else {
            query.append("return $item");
        }

        return query.toString();
    }

    /**
     * Method to build an XQuery query with complex filters (such as >, <, !=) and additional conditions.
     *
     * @param collectionPath The path of the collection in the eXist-db database.
     * @param filters        The filters to be applied in the XQuery (can use <, >, !=, etc.).
     * @return The constructed XQuery.
     */
    public static String buildQueryWithComplexFilters(String collectionPath, String[] filters) {
        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("') ");

        // Add the complex filters to the WHERE clause
        if (filters != null && filters.length > 0) {
            query.append("where ");
            for (int i = 0; i < filters.length; i++) {
                String[] parts = filters[i].split(" ");
                if (parts.length != 3) continue; // Expecting "field operator value"

                query.append("$item/").append(parts[0]).append(" ").append(parts[1]).append(" '").append(parts[2]).append("'");

                if (i < filters.length - 1) {
                    query.append(" and ");
                }
            }
        }

        query.append("return $item");
        return query.toString();
    }
}

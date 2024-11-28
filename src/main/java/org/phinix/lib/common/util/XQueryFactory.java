package org.phinix.lib.common.util;

/**
 * XQueryBuilder es una clase responsable de construir consultas XQuery.
 * No se encarga de ejecutar las consultas ni de mapear los resultados,
 * solo de generar la cadena de consulta con base en los filtros proporcionados.
 */
public class XQueryFactory {

    /**
     * Método único para construir una consulta XQuery.
     *
     * @param collectionPath El path de la colección en la base de datos eXist-db.
     * @param filters        Los filtros que se aplicarán en la consulta XQuery.
     * @return La consulta XQuery construida.
     */
    public static String buildQuery(String collectionPath, String... filters) {
        StringBuilder query = new StringBuilder();
        query.append("for $item in collection('").append(collectionPath).append("') ");

        if (filters.length > 0) {
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
        query.append("return $item");

        return query.toString();
    }
}


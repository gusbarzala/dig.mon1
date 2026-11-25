fun registrarEventoEvolucion(etapa: String, detalle: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", etapa)
            put("nivel", "Mega")
            put("estado", detalle)
        }
        db.insert(TABLE_CRIATURAS, null, values)
    }

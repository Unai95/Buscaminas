
class Celda {
    var esMina: Boolean = false
        private set
    var estaTapada: Boolean = true
        private set
    var tieneBandera: Boolean = false
        private set
    var minasVecinas: Int = 0
        private set


    internal fun revelar()            { estaTapada   = false }
    internal fun marcarMina()         { esMina       = true  }
    internal fun ponerBandera()       { tieneBandera = true  }
    internal fun quitarBandera()      { tieneBandera = false }
    internal fun fijarVecinas(n: Int) { minasVecinas = n     }
}


class Buscaminas(
    val filas: Int,
    val columnas: Int,
    val numMinas: Int
) {

    private lateinit var tablero: Array<Array<Celda>>

    var juegoTerminado: Boolean = false
        private set
    var victoria: Boolean = false
        private set

    // Vista de solo lectura: List inmutable; Celda con private set.
    val tableroVisible: List<List<Celda>>
        get() = tablero.map { fila -> fila.toList() }

    init {

        require(filas >= 1 && columnas >= 1) {
            "El numero de filas y columnas debe ser >= 1 " +
                    "(recibido: filas=$filas, columnas=$columnas)."
        }
        require(numMinas < filas * columnas) {
            "El numero de minas ($numMinas) debe ser menor que el " +
                    "total de celdas (${filas * columnas})."
        }

        tablero = Array(filas) { Array(columnas) { Celda() } }
        colocarMinas()
        calcularMinasVecinas()
    }


    private fun colocarMinas() {
        (0 until filas * columnas)
            .shuffled()
            .take(numMinas)
            .forEach { pos ->
                tablero[pos / columnas][pos % columnas].marcarMina()
            }
    }


    private fun calcularMinasVecinas() {
        for (f in 0 until filas) {
            for (c in 0 until columnas) {
                if (!tablero[f][c].esMina) {
                    val count = vecinosDe(f, c).count { it.esMina }
                    tablero[f][c].fijarVecinas(count)
                }
            }
        }
    }


    private fun vecinosDe(fila: Int, col: Int): List<Celda> =
        (-1..1).flatMap { df ->
            (-1..1).mapNotNull { dc ->
                if (df == 0 && dc == 0) return@mapNotNull null
                val nf = fila + df
                val nc = col  + dc
                if (nf in 0 until filas && nc in 0 until columnas)
                    tablero[nf][nc]
                else null
            }
        }


    fun destapar(fila: Int, columna: Int) {
        if (juegoTerminado) return
        if (fila !in 0 until filas || columna !in 0 until columnas) return

        val celda = tablero[fila][columna]
        if (!celda.estaTapada || celda.tieneBandera) return

        celda.revelar()

        if (celda.esMina) {
            juegoTerminado = true
            victoria       = false
            revelarTodasLasMinas()
            return
        }

        if (celda.minasVecinas == 0) {
            for (df in -1..1) {
                for (dc in -1..1) {
                    if (df == 0 && dc == 0) continue
                    val nf = fila + df
                    val nc = columna + dc
                    if (nf in 0 until filas && nc in 0 until columnas
                        && tablero[nf][nc].estaTapada
                        && !tablero[nf][nc].tieneBandera
                    ) {
                        destapar(nf, nc)   // recursion
                    }
                }
            }
        }

        comprobarVictoria()
    }


    fun toggleBandera(fila: Int, columna: Int) {
        if (juegoTerminado) return
        if (fila !in 0 until filas || columna !in 0 until columnas) return
        val celda = tablero[fila][columna]
        if (!celda.estaTapada) return
        if (celda.tieneBandera) celda.quitarBandera() else celda.ponerBandera()
    }



    private fun comprobarVictoria() {
        val gana = tablero.all { fila ->
            fila.all { celda -> celda.esMina || !celda.estaTapada }
        }
        if (gana) {
            juegoTerminado = true
            victoria       = true
        }
    }

    private fun revelarTodasLasMinas() {
        tablero.forEach { fila ->
            fila.forEach { celda -> if (celda.esMina) celda.revelar() }
        }
    }
}
class Celda {
    var esMina = false
        private set
    var estaTapada = true
        private set
    var tieneBandera = false
        private set
    var minasVecinas = 0
        private set


    internal fun ponerMina() { esMina = true }

    internal fun destapar() { estaTapada = false }

    internal fun ponerBandera() { tieneBandera = true }

    internal fun quitarBandera() { tieneBandera = false }

    internal fun setMinasVecinas(n: Int) { minasVecinas = n }
}

class Buscaminas(val filas: Int, val columnas: Int, val numMinas: Int) {

    private val tablero = Array(filas) { Array(columnas) { Celda() } }

    var juegoTerminado = false
        private set
    var victoria = false
        private set



    init {
        // Comprobamos que filas y columnas sean válidas y que no haya demasiadas minas

        require(filas >= 1 && columnas >= 1) {
            "Filas y columnas deben ser mayores que 0"
        }
        require(numMinas < filas * columnas) {
            "Hay demasiadas minas"
        }

        //Despúes de validar, generamos el tablero
        colocarMinas()
        calcularMinasVecinas()
    }

    //Obtenemos el tablero desde fuera pra imprimirlo
    fun obtenerTablero(): Array<Array<Celda>> = tablero

    private fun colocarMinas() {
        var colocadas = 0


        //Seguimos hasta colocar todas las minas
        while (colocadas < numMinas) {
            val f = (0 until filas).random()
            val c = (0 until columnas).random()

            if (!tablero[f][c].esMina) {
                tablero[f][c].ponerMina()
                colocadas++
            }
        }
    }

    private fun calcularMinasVecinas() {
        for (f in 0 until filas) {
            for (c in 0 until columnas) {
                if (!tablero[f][c].esMina) {
                    var contador = 0

                    //Justo lo que vimos la evaluación anterior

                    for (df in -1..1) {
                        for (dc in -1..1) {
                            if (df == 0 && dc == 0) continue

                            val nf = f + df
                            val nc = c + dc

                            if (nf in 0 until filas && nc in 0 until columnas) {
                                if (tablero[nf][nc].esMina) {
                                    contador++
                                }
                            }
                        }
                    }

                    tablero[f][c].setMinasVecinas(contador)
                }
            }
        }
    }

    fun destapar(fila: Int, columna: Int) {
        // Si la partida ya terminó, no hacemos nada
        if (juegoTerminado) return
        // Si la posición no existe, salimos
        if (fila !in 0 until filas || columna !in 0 until columnas) return

        val celda = tablero[fila][columna]

        // Si ya está destapada o tiene bandera, no se toca
        if (!celda.estaTapada || celda.tieneBandera) return

        celda.destapar()

        // Si era una mina, se acaba la partida
        if (celda.esMina) {
            juegoTerminado = true
            victoria = false
            revelarMinas()
            return
        }

        // Si no tiene minas alrededor, destapamos también las vecinas
        if (celda.minasVecinas == 0) {
            for (df in -1..1) {
                for (dc in -1..1) {
                    if (df == 0 && dc == 0) continue

                    val nf = fila + df
                    val nc = columna + dc

                    if (nf in 0 until filas && nc in 0 until columnas) {
                        if (tablero[nf][nc].estaTapada && !tablero[nf][nc].tieneBandera) {
                            // Aquí usamos recursión para que se sigan abriendo huecos automáticamente
                            destapar(nf, nc)
                        }
                    }
                }
            }
        }
        // Después de cada jugada comprobamos si ya ganó
        comprobarVictoria()
    }

    fun gestionarBandera(fila: Int, columna: Int) {
        // Si la partida terminó, no dejamos hacer nada más
        if (juegoTerminado) return
        // Si la posición no es válida, salimos
        if (fila !in 0 until filas || columna !in 0 until columnas) return

        val celda = tablero[fila][columna]

        // Solo se pueden poner banderas en celdas tapadas
        if (!celda.estaTapada) return

        // Si ya tenía bandera la quitamos y si no, la ponemos
        if (celda.tieneBandera) {
            celda.quitarBandera()
        } else {
            celda.ponerBandera()
        }
    }

    private fun comprobarVictoria() {
        // Recorremos todas las celdas
        for (fila in tablero) {
            for (celda in fila)
            {   // Si encontramos una celda que no es mina y sigue tapada,
                // significa que todavía no se ha ganado
                if (!celda.esMina && celda.estaTapada) {
                    return
                }
            }
        }


        // Si no quedaba ninguna celda segura por destapar, ha ganado
        juegoTerminado = true
        victoria = true
    }

    private fun revelarMinas() {
        // Cuando se pierde, destapamos todas las minas
        for (fila in tablero) {
            for (celda in fila) {
                if (celda.esMina) {
                    celda.destapar()
                }
            }
        }
    }
}
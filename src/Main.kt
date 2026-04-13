fun main() {
    mostrarBienvenida()


    val (filas, columnas, minas) = pedirConfiguracion()

    val juego: Buscaminas = try {
        Buscaminas(filas, columnas, minas)
    } catch (e: IllegalArgumentException) {
        println("\n⚠  Error al crear la partida: ${e.message}")
        return
    }


    while (!juego.juegoTerminado) {
        println()
        mostrarTablero(juego)
        procesarTurno(juego)
    }


    println()
    mostrarTablero(juego)
    if (juego.victoria) {
        println("╔══════════════════════════════╗")
        println("║  🎉  ¡ENHORABUENA, GANASTE!  ║")
        println("╚══════════════════════════════╝")
    } else {
        println("╔════════════════════════════════════╗")
        println("║  💥  ¡BOOM! Pisaste una mina.      ║")
        println("║       GAME OVER                    ║")
        println("╚════════════════════════════════════╝")
    }
}

// ── Bienvenida ────────────────────────────────────────────────
fun mostrarBienvenida() {
    println("╔══════════════════════════════╗")
    println("║       B U S C A M I N A S    ║")
    println("╚══════════════════════════════╝")
    println()
    println("Comandos durante la partida:")
    println("  D  →  Destapar celda")
    println("  B  →  Colocar / quitar bandera (🚩)")
    println()
}


data class Configuracion(val filas: Int, val columnas: Int, val minas: Int)

fun pedirConfiguracion(): Configuracion {
    println("──── Configuración de la partida ────")
    val filas    = leerEntero("Número de filas    (por defecto 9): ",    9)
    val columnas = leerEntero("Número de columnas (por defecto 9): ",    9)
    val minas    = leerEntero("Número de minas    (por defecto 10): ", 10)
    println()
    return Configuracion(filas, columnas, minas)
}

fun leerEntero(prompt: String, defecto: Int): Int {
    print(prompt)
    return readLine()?.trim()?.toIntOrNull() ?: defecto
}


fun mostrarTablero(juego: Buscaminas) {
    val tablero = juego.tableroVisible   // lectura permitida

    // Cabecera de columnas
    val anchoFila = juego.filas.toString().length          // dígitos para índice de fila
    val pad = " ".repeat(anchoFila + 2)
    print(pad)
    for (c in 0 until juego.columnas) print("%3d".format(c))
    println()
    print(pad)
    println("───".repeat(juego.columnas))

    // Filas
    for (f in 0 until juego.filas) {
        print("%${anchoFila}d │".format(f))
        for (c in 0 until juego.columnas) {
            val celda = tablero[f][c]
            val simbolo: String = when {
                celda.tieneBandera            -> " 🚩"
                celda.estaTapada              -> " . "
                celda.esMina                  -> " * "
                celda.minasVecinas == 0       -> "   "
                else                          -> " ${celda.minasVecinas} "
            }
            print(simbolo)
        }
        println()
    }
    println()

    val banderasColocadas = tablero.sumBy { fila -> fila.count { it.tieneBandera } }
    println("  Minas: ${juego.numMinas}  |  Banderas colocadas: $banderasColocadas  |  " +
            "Restantes: ${juego.numMinas - banderasColocadas}")
}


fun procesarTurno(juego: Buscaminas) {
    println("────────────────────────────────────")
    println("Acción  →  [D] Destapar   [B] Bandera")

    val accion = leerAccion()
    val fila   = leerEntero("  Fila    : ", -1)
    val col    = leerEntero("  Columna : ", -1)

    when (accion) {
        'D' -> juego.destapar(fila, col)      // única forma de modificar el tablero
        'B' -> juego.toggleBandera(fila, col) // única forma de modificar banderas
    }
}

fun leerAccion(): Char {
    while (true) {
        print("Acción [D/B]: ")
        val entrada = readLine()?.trim()?.uppercase() ?: ""
        if (entrada.isNotEmpty() && entrada[0] in listOf('D', 'B')) return entrada[0]
        println("  ⚠  Escribe D para destapar o B para bandera.")
    }
}
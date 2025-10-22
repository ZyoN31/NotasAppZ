package com.zyondev.notasapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotaDao {
    @Insert
    suspend fun insertar(nota: Nota): Long

    @Query("SELECT * FROM notas ORDER BY id DESC")
    suspend fun obtenerNotas(): List<Nota>

    @Query("DELETE FROM notas")
    suspend fun borrarTodas()
}
package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getElections(): LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table where id = :electionId")
    suspend fun getElectionById(electionId : Int): Election

    //TODO: Add delete query
    @Query("DELETE FROM election_table where id = :electionId")
    suspend fun deleteElectionById(electionId : Int)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clearElections()

    @Query("SELECT EXISTS (SELECT * FROM election_table WHERE id = :electionId)")
    fun existElectionById(electionId : Int) : LiveData<Boolean>

}
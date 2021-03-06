package com.leavelive.diary.repository

import com.leavelive.diary.model.Diary
import com.leavelive.diary.model.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {
    fun findByUserIdAndDate(userId: String, date: LocalDate): Optional<Diary>
    fun findAllByStatusOrderByDateDesc(status: Status) : List<Diary>
    fun findAllByUserIdOrderByDateDesc(userId: String) : List<Diary>
    fun findAllByStatusAndTagContainsOrderByDateDesc(status: Status, tag: String): List<Diary>
}
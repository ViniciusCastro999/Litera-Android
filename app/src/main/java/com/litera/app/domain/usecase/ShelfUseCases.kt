package com.litera.app.domain.usecase

import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShelfUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    operator fun invoke(): Flow<List<ShelfBook>> = repository.observeShelf()
}

class ObserveShelfItemUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    operator fun invoke(volumeId: String): Flow<ShelfBook?> = repository.observeShelfItem(volumeId)
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    suspend operator fun invoke(book: Book) = repository.toggleFavorite(book)
}

class StartReadingUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    suspend operator fun invoke(book: Book, totalPages: Int) = repository.startReading(book, totalPages)
}

class UpdateReadingProgressUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    suspend operator fun invoke(volumeId: String, currentPage: Int, totalPages: Int) =
        repository.updateProgress(volumeId, currentPage, totalPages)
}

class MarkAsReadUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    suspend operator fun invoke(volumeId: String) = repository.markAsRead(volumeId)
}

class RemoveFromShelfUseCase @Inject constructor(
    private val repository: ShelfRepository
) {
    suspend operator fun invoke(volumeId: String) = repository.removeFromShelf(volumeId)
}

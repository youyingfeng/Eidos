package org.eidos.reader.repository.paging.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eidos.reader.model.domain.WorkBlurb
import org.eidos.reader.model.ui.WorkBlurbUiModel
import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.WorkFilterRequest

private const val AO3_STARTING_PAGE_INDEX = 1

/**
 * Paging source that retrieves a page containing a [WorkBlurbUiModel.SeparatorItem] at the top to
 * denote the page number, and up to 20 [WorkBlurbUiModel.WorkBlurbItem]s.
 *
 * The page size does not matter, as returning a different number of items is valid.
 */
class WorkBlurbUiModelPagingSource
    constructor(
        private val ao3Service: AO3,
        private val workFilterRequest: WorkFilterRequest
    )
    : PagingSource<Int, WorkBlurbUiModel>()
{
    /**
     * Ignores almost every parameter out there except for key.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WorkBlurbUiModel> {
        val position = params.key ?: AO3_STARTING_PAGE_INDEX
        workFilterRequest.pageNumber = position

        // get the page of WorkBlurbs from AO3
        val workBlurbs = try {
            withContext(Dispatchers.IO) { ao3Service.getWorkBlurbs(workFilterRequest) }
        } catch (exception: Network.ServerException) { // FIXME: change this to catch everything before production
            // the only errors that *should* happen are network errors
            return LoadResult.Error(exception)
        } catch (exception: Network.NetworkException) {
            return LoadResult.Error(exception)
        }

        // map them to WorkBlurbUiModels
        val workBlurbUiModels = workBlurbs
            .map<WorkBlurb, WorkBlurbUiModel> { workBlurb -> WorkBlurbUiModel.WorkBlurbItem(workBlurb) }
            .toMutableList()

        // then add a page separator at the front if list is not empty and not first page
        // the list not empty condition prevents loading an empty page with only a separator
        if (position > AO3_STARTING_PAGE_INDEX && workBlurbs.isNotEmpty()) {
            workBlurbUiModels.add(0, WorkBlurbUiModel.SeparatorItem("Page $position"))
        }

        val nextKey = if (workBlurbs.isEmpty()) {
            // AO3 will show a page with no blurbs if page limit is exceeded
            null
        } else {
            position + 1
        }

        return LoadResult.Page(
            data = workBlurbUiModels,
            prevKey = if (position == AO3_STARTING_PAGE_INDEX) null else position,
            nextKey = nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, WorkBlurbUiModel>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            return@let anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

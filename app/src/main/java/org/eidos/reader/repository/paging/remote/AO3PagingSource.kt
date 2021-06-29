package org.eidos.reader.repository.paging.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eidos.reader.model.WorkBlurb
import org.eidos.reader.network.Network
import org.eidos.reader.remote.AO3
import org.eidos.reader.remote.requests.WorkFilterRequest
import timber.log.Timber

private const val AO3_STARTING_PAGE_INDEX = 1


class AO3PagingSource
    constructor(
        private val ao3Service: AO3,
        private val workFilterRequest: WorkFilterRequest
    )
    : PagingSource<Int, WorkBlurb>()
{
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WorkBlurb> {
        val position = params.key ?: AO3_STARTING_PAGE_INDEX
        workFilterRequest.pageNumber = position

        val workBlurbs = try {
            withContext(Dispatchers.IO) { ao3Service.getWorkBlurbs(workFilterRequest) }
        } catch (exception: Network.ServerException) { // FIXME: change this to catch everything before production
            // the only errors that *should* happen are network errors
            return LoadResult.Error(exception)
        } catch (exception: Network.NetworkException) {
            return LoadResult.Error(exception)
        }

        val nextKey = if (workBlurbs.isEmpty()) {
            // AO3 will show a page with no blurbs if page limit is exceeded
            null
        } else {
            position + 1
        }

        return LoadResult.Page(
            data = workBlurbs,
            prevKey = if (position == AO3_STARTING_PAGE_INDEX) null else position,
            nextKey = nextKey
        )

    }

    override fun getRefreshKey(state: PagingState<Int, WorkBlurb>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            return@let anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    }
}

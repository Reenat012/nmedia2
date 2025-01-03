//package ru.netology.nmedia.repository
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import retrofit2.HttpException
//import ru.netology.nmedia.Post
//import ru.netology.nmedia.api.PostApiService
//import java.io.IOException
//
//class PostPagingSource(private val apiService: PostApiService): PagingSource<Long, Post>() {
//    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null
//
//    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
//        try {
//            val result = when (params) {
//                is LoadParams.Refresh -> apiService.getLatest(params.loadSize)
//                //скролл вниз
//                is LoadParams.Append -> {
//                    apiService.getBefore(id = params.key, count = params.loadSize)
//                }
//                //когда пользовател скроллит вверх, у него не будет загружаться новая страница
//                is LoadParams.Prepend -> return LoadResult.Page(
//                    data = emptyList(), nextKey = null, prevKey = params.key
//                )
//            }
//
//            if (!result.isSuccessful) {
//                throw HttpException(result)
//            }
//
//            val data = result.body().orEmpty()
//
//            return LoadResult.Page(
//                data = data,
//                prevKey = params.key,
//                nextKey = data.lastOrNull()?.id
//            )
//        } catch (e: IOException) {
//            return LoadResult.Error(e)
//        }
//    }
//}
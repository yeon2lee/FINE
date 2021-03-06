package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentCommunityMainBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.MyPage.ManagePostActivity
import retrofit2.Call
import retrofit2.Response

class CommunityMainFragment : Fragment() {

    private var _binding: FragmentCommunityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    /*
    private val postViewModel: PostViewModel by lazy{
        ViewModelProvider(this).get(PostViewModel::class.java)
    }

     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //RetrofitManager.instance.viewMainCommunity()

        // API 호출
        viewMainCommunity()
        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var post: Post
        private val postTitle: TextView =itemView.findViewById(R.id.mainpost_title)
        private val commentNum: TextView =itemView.findViewById(R.id.mainpost_comment)
        init{
            postTitle.setOnClickListener(this)
        }
        fun bind(post: Post) {
            this.post=post
            postTitle.text=this.post.title
            commentNum.text=this.post.commentCount
        }
        override fun onClick(p0: View?) {
            //글 세부 API 호출
            /*
            RetrofitManager.instance.viewMainPosting("1", completion = {
                it ->
                Log.d("retrofit", "메인 글 세부 API 호출 // $it ")
            })

             */
            viewMainPosting("1")
            /*
            var postDetail= Intent(getActivity(), PostDetail_Main::class.java)
            postDetail.putExtra("nickname", post.nickname)
            //postDetail.putExtra("profileID", post.profileID)
            postDetail.putExtra("title", post.title)
            postDetail.putExtra("content", post.content)
            postDetail.putExtra("capacity", post.capacity)
            //postDetail.putExtra("participants", post.participants)
            startActivity(postDetail)

             */
        }
    }
    inner class MyAdapter(val list:List<Post>): RecyclerView.Adapter<MyViewHolder>() {//, Filterable
/*
        val unFilteredList = list //⑴
        var filteredList = list

        override fun getItemCount(): Int = filteredList.size
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charString = constraint.toString()
                    filteredList = if (charString.isEmpty()) { //⑶
                        unFilteredList
                    } else {
                        var filteringList = ArrayList<Post>()
                        for (item in unFilteredList) {
                            if (!item.groupCheck) filteringList.add(item) //그룹 글이 아니면 추가
                        }
                        filteringList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                }
                override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                    filteredList = results.values as ArrayList<Post>
                    notifyDataSetChanged()
                }
            }


        }*/
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_postlist_main, parent, false)
            return MyViewHolder(view)
            }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            //val post=filteredList[position]
            val post=list[position]
            holder.bind(post)
        }


    }

    private fun viewMainCommunity(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMainCommunity() ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<List<Post>>{
            //응답성공
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "메인커뮤니티목록 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                //adapter.filter.filter("1")
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }
            //응답실패
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "메인커뮤니티목록 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewMainPosting(postingId:String?){ // postingId 받아와야 할 듯
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:String= postingId ?:""
        val call = iRetrofit?.viewMainPosting(PostingID = term) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", response.body().toString())
                //completion(response.body().toString())

                val postDetail= Intent(activity, PostDetail_Main::class.java)
                postDetail.putExtra("nickname", "user")
                postDetail.putExtra("title", response.body()!!.title)
                postDetail.putExtra("content", response.body()!!.content)
                postDetail.putExtra("comments", response.body()!!.comments)
                response.body()!!.comments
                postDetail.putExtra("capacity", response.body()!!.capacity)
                startActivity(postDetail)
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



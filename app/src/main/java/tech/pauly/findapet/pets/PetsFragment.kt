package tech.pauly.findapet.pets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bleacherreport.velocidapter.PetListAdapterDataList
import com.bleacherreport.velocidapter.attachPetListAdapter
import com.bleacherreport.velocidapterandroid.AdapterDataTarget
import kotlinx.android.synthetic.main.fragment_pets.*
import org.koin.android.viewmodel.ext.android.viewModel
import tech.pauly.findapet.R
import tech.pauly.findapet.extensions.observe
import tech.pauly.findapet.GridItemDecoration

const val PetListAdapter = "PetListAdapter"

class PetsFragment : Fragment() {

    private val petsViewModel: PetsViewModel by viewModel()

    private var adapter: AdapterDataTarget<PetListAdapterDataList>? = null
    private var recyclerItemWidth: Int? = null

    private var refreshing: Boolean = false
        set(value) {
            petsSwipeRefresh?.isRefreshing = value
            field = value
        }

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petsViewModel.refreshPets()
        refreshing = true
        lifecycle.addObserver(petsViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefreshView()
        setupRecyclerView()
        observeLiveData()
    }

    private fun setupRecyclerView() {
        val cnxt = context ?: return
        petsRecyclerView.layoutManager = GridLayoutManager(cnxt, 2)
        petsRecyclerView.addItemDecoration(GridItemDecoration(cnxt, GridLayoutManager.HORIZONTAL))

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val edgeMargin = 16
        val centerMargin = 8
        val marginSize = (edgeMargin * 2 + centerMargin) * resources.displayMetrics.density
        recyclerItemWidth = (screenWidth - marginSize).toInt() / 2
        adapter = petsRecyclerView.attachPetListAdapter()
    }
    //endregion

    private fun observeLiveData() {
        petsViewModel.petListState.observe(this) { state ->
            refreshing = false
            state.petList?.let { pets ->
                val items = pets.map { PetViewItem(it, recyclerItemWidth ?: 0) }
                adapter?.updateDataset(PetListAdapterDataList().apply {
                    addListOfPetViewItem(items)
                })
            }
            state.error?.let {
                // show error
            }
        }
    }

    private fun setupRefreshView() {
        refreshing = refreshing // to inform the refresh view of current refresh state when it's constructed
        petsSwipeRefresh.setColorSchemeResources(R.color.white)
        petsSwipeRefresh.setProgressBackgroundColorSchemeColor(context?.getColor(R.color.colorAccent) ?: 0)
        petsSwipeRefresh.setOnRefreshListener {
            petsViewModel.refreshPets()
        }
    }
}

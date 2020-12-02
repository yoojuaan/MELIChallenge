package com.jphernandez.melichallenge.productDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jphernandez.melichallenge.MeliChallengeApplication
import com.jphernandez.melichallenge.Product
import com.jphernandez.melichallenge.R
import com.jphernandez.melichallenge.helpers.displayThumbnail
import com.jphernandez.melichallenge.productList.getViewModel
import com.jphernandez.melichallenge.repositories.ProductsRepository
import javax.inject.Inject

class ProductDetailFragment: Fragment() {

    @Inject
    lateinit var productsRepository: ProductsRepository

    private val viewModel: ProductDetailVM by lazy { getViewModel { ProductDetailVM(productsRepository) } }

    private var productId: String? = null

    private lateinit var productImage: ImageView
    private lateinit var productTitle: TextView
    private lateinit var productPrice: TextView
    private lateinit var productShip: TextView
    private lateinit var productDescription: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MeliChallengeApplication.appComponent.inject(this)
        return inflater.inflate(R.layout.product_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getString(PRODUCT_ID)

        viewModel.productLiveData.observe(viewLifecycleOwner, Observer {
            loadView(it, view)
            Log.d("ProductDetailFragment", it.toString())
        })

        productId?.let {
            viewModel.getProductById(it)
        }
    }

    private fun loadView(product: Product, view: View) {
        productImage = view.findViewById(R.id.product_image)
        productTitle = view.findViewById(R.id.product_name)
        productPrice = view.findViewById(R.id.product_price)
        productShip = view.findViewById(R.id.product_ship)
        productDescription = view.findViewById(R.id.product_description)

       val price = "$ ${product.price}"
        productTitle.text = product.title
        productPrice.text = price
        productShip.visibility = if(product.isFreeSheeping) View.VISIBLE else View.GONE
        var imageUrl: String? = null
        product.pictures?.forEach { if(it.id == product.thumbnailId) imageUrl = it.getURL() }
        displayThumbnail(imageUrl, productImage)
    }

    companion object {
        const val PRODUCT_ID = "PRODUCT_ID"

        fun getBundle(productId: String): Bundle {
            val bundle = Bundle()
            bundle.putString(PRODUCT_ID, productId)
            return bundle
        }
    }

}
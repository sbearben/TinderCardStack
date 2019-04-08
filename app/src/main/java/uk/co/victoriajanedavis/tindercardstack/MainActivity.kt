package uk.co.victoriajanedavis.tindercardstack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.victoriajanedavis.tindercardstack.cardView.CardStackLayoutManager
import uk.co.victoriajanedavis.tindercardstack.data.CardStackAdapter
import uk.co.victoriajanedavis.tindercardstack.data.generateDummyUsers

class MainActivity : AppCompatActivity() {

    private val adapter = CardStackAdapter()
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardStackView.layoutManager = CardStackLayoutManager(this)
        cardStackView.adapter = adapter
        adapter.submitList(generateDummyUsers())

        skipButton.setOnClickListener {
            cardStackView.leftSwipe()
        }
        likeButton.setOnClickListener {
            cardStackView.rightSwipe()
        }
        rewindButton.setOnClickListener {
            cardStackView.rewind()
        }

        /*
        handler.postDelayed({
            adapter.submitList(generateDummyUsers())
        }, 4000)
        */
    }
}
package com.example.blackjack

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.blackjack.ui.theme.BlackJackTheme
import java.util.*

class MainActivity : ComponentActivity() {
    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val bet  = findViewById<Button>(R.id.bet)
        val hit = findViewById<Button>(R.id.hit)
        val stay = findViewById<Button>(R.id.Stay)
        val bA = findViewById<EditText>(R.id.betamount)
        val m = findViewById<TextView>(R.id.money)
        val dealer = findViewById<TextView>(R.id.tvdealer_cards)
        val player = findViewById<TextView>(R.id.tvdealer_cards)
        val p_cards = findViewById<LinearLayout>(R.id.player_cards)
        val d_card = findViewById<LinearLayout>(R.id.dealer_cards)
        var playercards = Array<Int>(10){0}
        var dealercards = Array<Int>(10){0}
        var i = 0 // counter to keep track of elements in playercards array
        var j = 0 // counter to keep track of elements in dealercards array


        bet.setOnClickListener()
        {
            var betAmount: Int = bA.text.toString().toInt()
            var money: Double = m.text.toString().toDouble()
            p_cards.removeAllViews()
            d_card.removeAllViews()
            if (betAmount > money){
                Toast.makeText(this,"Not Enough Money",Toast.LENGTH_SHORT).show()
            }
            else{
                 money = money - betAmount
                 m.text = "${money}"
                //Dealer Card
                val (cardval,cardResId) = randomCardGenerator()
                //Dealer Card 1
                addCardToLayout(d_card, cardResId)
                dealer.text = "${cardval} "
                dealercards[j++] = cardval

                //Player Card 1
                var (card,ResId) = randomCardGenerator()
                addCardToLayout(p_cards, ResId)
                player.text = "${card} "
                playercards[i++] = card
                //Player Card 2
                var (card1,resId1) = randomCardGenerator()
                addCardToLayout(p_cards, resId1)
                println("2 Cards generated $card & $card1")
                playercards[i++] = card1
                var playercard = player.text.toString()
                player.text = playercard + "${card1} "
                //calculating the score of player cards
                var sum = 0
                for ( card in playercards){
                    var c = card
                    sum += c
                }
                if(sum == 21){
                    Toast.makeText(this,"You Win",Toast.LENGTH_SHORT).show()
                    money = money +( 1.5 * betAmount)
                    m.text = "$money"
                    resetdealerandplayer(playercards,dealercards)
                    i = 0
                    j = 0
                    return@setOnClickListener
                }
                hit.visibility = View.VISIBLE
                stay.visibility = View.VISIBLE
            }
        }

        hit.setOnClickListener(){

            var money: Double = m.text.toString().toDouble()
            var betAmount = bA.text.toString().toInt()

            var playercard = player.text.toString()
            var (card,resId) = randomCardGenerator()
            playercards[i++] = card
            player.text = playercard + "$card "
            addCardToLayout(p_cards,resId)
            var sum = 0
            for ( card in playercards){
                var c = card
                sum += c
            }
            if(sum > 21){
                Toast.makeText(this,"You Loose",Toast.LENGTH_SHORT).show()
                resetdealerandplayer(playercards,dealercards)
                hit.visibility = View.GONE
                stay.visibility = View.GONE
                resetdealerandplayer(playercards,dealercards)
                i = 0
                j = 0

            }
            else if (sum == 21){
                Toast.makeText(this,"You Win",Toast.LENGTH_SHORT).show()
                resetdealerandplayer(playercards,dealercards)
                i = 0
                j = 0
                money = money +( 1.5 * betAmount)
                m.text = "$money"
                hit.visibility = View.GONE
                stay.visibility = View.GONE
            }

        }
        stay.setOnClickListener(){
            var (card,resId) = randomCardGenerator()
            addCardToLayout(d_card,resId)
            var money = m.text.toString().toDouble()
            var bet = bA.text.toString().toInt()
            dealercards[j++] = card
            var dealercard = dealer.text.toString() + "$card "
            hit.visibility = View.GONE
            stay.visibility = View.GONE
            dealer.text = dealercard

            var dsum = 0
            for(card in dealercards){
                var c = card
                dsum += c
            }
            println(dsum)
            if(dsum <= 16){
                var (card1,resId1) = randomCardGenerator()
                addCardToLayout(d_card,resId1)
                dealercard = dealercard + "$card1 "
                dealer.text = dealercard
                dealercards[j++] = card1
                dsum = 0
                Toast.makeText(this,"Dealer Picking a New Card",Toast.LENGTH_SHORT).show()
                for( card in dealercards){
                    var c = card
                    dsum += c
                }
            }
            var ssum = 0
            for(card in playercards){
                var c = card
                ssum += c
            }
            if (dsum > 21){
                Toast.makeText(this, "You Win ${2*bet}", Toast.LENGTH_SHORT).show()
                money = money + (2 * bet)
                m.text = "$money"
                hit.visibility = View.GONE
                stay.visibility = View.GONE
                resetdealerandplayer(playercards,dealercards)
                i = 0
                j = 0
            }
            else if(dsum == 21){
                Toast.makeText(this, "You Loose", Toast.LENGTH_SHORT).show()
                resetdealerandplayer(playercards,dealercards)
                i = 0
                j = 0
            }
            else if( dsum < ssum ){
                Toast.makeText(this, "You Win", Toast.LENGTH_SHORT).show()
                money = money + (2 * bet)
                m.text = "$money"
                resetdealerandplayer(playercards,dealercards)
                i = 0
                j = 0
            }
            else{
                Toast.makeText(this, "You Loose", Toast.LENGTH_SHORT).show()
                resetdealerandplayer(playercards,dealercards)
                i = 0
                j = 0
            }
        }


    }

    private fun resetdealerandplayer(playercards : Array<Int>, dealercards : Array<Int>) {
        for(i in 0..9){
            playercards[i] = 0
            dealercards[i] = 0
        }
    }

    @SuppressLint("DiscouragedApi")
    fun randomCardGenerator(): Pair<Int,Int> {
        val cardval = arrayOf("1","2","3","4","5","6","7","8","9","10","j","k","q")
        val cardsuit = arrayOf("c","d","h","s")
        val r = Random()
        var cv = cardval[r.nextInt(cardval.size)]
        var cardvalue = 0
        if(cv=="j"||cv == "k"||cv == "q"){
            cardvalue = 10
        }
        else{
            cardvalue = cv.toInt()
        }
        var cs = cardsuit[r.nextInt(cardsuit.size)]
        val cardImageResId = resources.getIdentifier(
            "${cs}${cv}",
            "drawable",
            packageName
        )
        return Pair(cardvalue,cardImageResId)


    }
    fun addCardToLayout(layout: LinearLayout, cardImageResId: Int) {
        val cardImageView = ImageView(this)
        cardImageView.setImageResource(cardImageResId)
        val params = LinearLayout.LayoutParams(
            200,
            300
        )
        params.marginEnd = 5
        cardImageView.layoutParams = params
        layout.addView(cardImageView)

        layout.requestLayout()
        layout.invalidate()

    }

}
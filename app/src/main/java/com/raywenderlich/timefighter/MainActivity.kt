package com.raywenderlich.timefighter

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    // setup countdown
    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 60000
    private var countDownInterval: Long = 1000
    private var timeLeft = 60
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

//These track the variables you want to save when the orientation changes
    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

//is the entry point to this Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//Calling the base’s implementation of onCreate()
        setContentView(R.layout.activity_main)//This line takes the Layout you created//
     //and puts it on your device screen by passing in the identifier for the Layout
        Log.d(TAG, "onCreate called. Score is: $score")

//searches through the activity_main
// Layout to find the View with the corresponding ID and provides a reference to it you store as a variable
        gameScoreTextView = findViewById(R.id.game_score_text_view)
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        tapMeButton = findViewById(R.id.tap_me_button)

//every time you click the button, tapMeButton.setOnClickListener loads the bounce animation inside anim
// and instructs the button to use that animation
        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this,
                R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
            resetGame()
        }
// make sure game doesn't reset when onCreate
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }
    }
    //restore game
    private fun restoreGame() {
        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = restoredTime

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {

                timeLeft = millisUntilFinished.toInt() / 1000

                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }
    //insert the value of score and timeleft
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeft")

    }
// a method used by the Activity to clean itself up when it is being destroyed
    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy called.")
    }
    //callback when it attempts to create the menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {// Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true

    }
    // tell your Activity what to do in case the item is clicked.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) {
            showInfo()
        }
        return true
    }
// keep track of the score at the top of MainActivity.kt and initialize it to 0
    private var score = 0

// connect views to variables
    // increment score logic
    //update score and retrieves score from string.xml
    private fun incrementScore() {
        score++

        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore
    // make sure the start
        if (!gameStarted){
            startGame()
        }

    }

    // reset game logic
    private fun resetGame() {
        // called initalScore to store the score as a string
        score = 0
        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore
        val initialTimeLeft = getString(R.string.time_left, 60)
        timeLeftTextView.text = initialTimeLeft

        // object and pass into the constructor initialCountDown and countDownInterval, set to 60000 and 10
        countDownTimer = object : CountDownTimer(initialCountDown,
            countDownInterval) {

            // onTick and onFinish. onTick is called at every interval you passed into the timer;  onTick also passes in a parameter called
            override fun onTick(millisUntilFinished: Long)
            {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(
                    R.string.time_left,
                    timeLeft
                )
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()

            }
        }
        // game has not started
        gameStarted = false

    }

    // start game logic
    private fun startGame() {
       countDownTimer.start()
        gameStarted = true

    }
    // end game logic
    private fun endGame() {
        Toast.makeText(
            this, getString(
                R.string.game_over_message,
                score
            ), Toast.LENGTH_LONG
        ).show()
        resetGame()
    }
    // creates two strings to use in the dialog, one for the title and one for the messag
    private fun showInfo() {
        val dialogTitle = getString(
            R.string.about_title,
            BuildConfig.VERSION_NAME
        )
        val dialogMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()


    }
}

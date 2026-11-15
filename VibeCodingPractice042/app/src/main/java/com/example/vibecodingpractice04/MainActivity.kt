package com.example.vibecodingpractice04 // 注意：请确保这里的包名和你的项目一致！

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var tvTitle: TextView
    private lateinit var dbHelper: DatabaseHelper

    // 存储按钮的二维数组 (5x5)
    private val buttons = Array(5) { arrayOfNulls<Button>(5) }
    // 存储每个格子的数字
    private val numbers = Array(5) { IntArray(5) }
    // 记录每个格子是否被点击过
    private val isMarked = Array(5) { BooleanArray(5) }

    private var currentRound = 0 // 当前回合数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化视图
        gridLayout = findViewById(R.id.gridLayout)
        tvTitle = findViewById(R.id.tvTitle)
        val btnStartGame = findViewById<Button>(R.id.btnStartGame)

        // 初始化数据库帮助类
        dbHelper = DatabaseHelper(this)

        // 开始新游戏的按钮点击事件
        btnStartGame.setOnClickListener {
            currentRound++
            startNewGame()
        }

        // 初始时启动一局游戏
        currentRound++
        startNewGame()
    }

    // 开始新游戏：生成随机数字并重置UI
    private fun startNewGame() {
        tvTitle.text = "Bingo - 第 $currentRound 回合"
        gridLayout.removeAllViews() // 清空之前的网格
        val usedNumbers = mutableSetOf<Int>() // 确保数字不重复

        // 遍历 5x5 的网格
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                // 生成 1-75 之间的不重复随机数 (Bingo 标准范围)
                var num: Int
                do {
                    num = Random.nextInt(1, 76)
                } while (usedNumbers.contains(num))
                usedNumbers.add(num)
                numbers[row][col] = num
                isMarked[row][col] = false

                // 动态创建按钮
                val button = Button(this).apply {
                    text = num.toString()
                    textSize = 14f
                    setBackgroundColor(Color.LTGRAY)
                    // 设置按钮点击逻辑
                    setOnClickListener {
                        if (!isMarked[row][col]) {
                            isMarked[row][col] = true
                            setBackgroundColor(Color.GREEN) // 点击后变绿
                            checkBingo(row, col) // 检查是否连成线
                        }
                    }
                }
                buttons[row][col] = button

                // 设置 GridLayout 的布局参数
                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)
                    width = 0
                    height = 0
                }
                gridLayout.addView(button, params)
            }
        }
    }

    // 检查是否达成 Bingo
    private fun checkBingo(clickedRow: Int, clickedCol: Int) {
        // 1. 检查行
        if ((0 until 5).all { isMarked[clickedRow][it] }) {
            gameWon()
            return
        }
        // 2. 检查列
        if ((0 until 5).all { isMarked[it][clickedCol] }) {
            gameWon()
            return
        }
        // 3. 检查主对角线
        if (clickedRow == clickedCol && (0 until 5).all { isMarked[it][it] }) {
            gameWon()
            return
        }
        // 4. 检查副对角线
        if (clickedRow + clickedCol == 4 && (0 until 5).all { isMarked[it][4 - it] }) {
            gameWon()
            return
        }
    }

    // 游戏胜利时调用：保存数据到 SQLite 并提示
    private fun gameWon() {
        // 获取第一行的五个数字作为存入数据库的示例 (满足题目要求)
        val bingoNumbersStr = (0 until 5).joinToString(",") { numbers[0][it].toString() }

        // 保存到数据库：用户名写死为 "Player1"
        dbHelper.insertGameRecord("Player1", currentRound, bingoNumbersStr)

        Toast.makeText(this, "Bingo! 数据已保存。", Toast.LENGTH_LONG).show()

        // 这里可以设置个延迟，或者让玩家自己点 "开始新游戏"
    }
}
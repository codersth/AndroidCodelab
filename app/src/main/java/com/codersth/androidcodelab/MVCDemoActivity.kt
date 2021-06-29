package com.codersth.androidcodelab

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.codersth.androidcodelab.databinding.ActivityMvcdemoBinding
import kotlinx.coroutines.*

/**
 * An example to show the minimum MVC model with a sample of stock in purchase scene. Coroutines used
 * for simulating the processing-block from the backend when purchasing.
 * @author zhanglei at 2021/06/29
 * @since 1.0
 */
class MVCDemoActivity : AppCompatActivity() {

    companion object {
        const val DEFAULT_STOCK = 50
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create databinding instance depend on layout.
        val binding = DataBindingUtil.setContentView<ActivityMvcdemoBinding>(this, R.layout.activity_mvcdemo)
        // Initialize a model with original stock 50.
        val model = ShopModel(DEFAULT_STOCK)
        binding.stockTv.text = "${model.stock}"
        binding.increaseBtn.setOnClickListener {
            // Invoke the model's function and pass a call back to receive the processed result in the scope of coroutines..
            lifecycleScope.launch {
                model.increaseStock {result ->
                    binding.stockTv.text = "$result"
                }
            }
        }
        binding.decreaseBtn.setOnClickListener {
            // As same the workflow with increase.
            lifecycleScope.launch {
                model.decreaseStock {result ->
                    binding.stockTv.text = "$result"
                }
            }
        }
        binding.platform = "CSDN"
        // Don't forget to binding its lifecycle with current component!!
        binding.lifecycleOwner = this
    }

}

/**
 * As the model in MVC pattern with both field and logic.
 */
private class ShopModel(var stock: Int) {

    companion object {
        const val TASK_DELAY = 1000L
    }

    private val task = OperationTask()

    suspend fun increaseStock(callback: ((result: Int) -> Unit)?) {
        callback?.invoke(task.increaseStock())
    }

    suspend fun decreaseStock(callback: ((result: Int) -> Unit)?) {
        callback?.invoke(task.decreaseStock())
    }

    /**
     * A task just demonstrate the backend.
     */
    inner class OperationTask {

        suspend fun increaseStock() = withContext(Dispatchers.IO) {
            delay(TASK_DELAY)
            ++ stock
        }

        suspend fun decreaseStock() = withContext(Dispatchers.IO) {
            delay(TASK_DELAY)
            print(Thread.currentThread())
            if (stock > 0) {
                -- stock
            } else {
                0
            }
        }
    }
}
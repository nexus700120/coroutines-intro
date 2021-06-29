package com.epa.coroutines.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.epa.coroutines.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.get


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    @Suppress("UNCHECKED_CAST")
    private val viewModel by viewModels<DashboardViewModel> {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DashboardViewModel(get(), get()) as T
            }
        }
    }

    private lateinit var pairSymbols: TextView
    private lateinit var pairName: TextView
    private lateinit var pairContainer: View

    private lateinit var progress: ProgressBar
    private lateinit var chart: LineChart

    private lateinit var errorText: TextView
    private lateinit var retryButton: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pairSymbols = view.findViewById(R.id.currency)
        pairName = view.findViewById(R.id.currency_name)
        pairContainer = view.findViewById(R.id.selected_currency)
        errorText = view.findViewById(R.id.error_text)
        retryButton = view.findViewById(R.id.retry_button)
        chart = view.findViewById(R.id.chart)
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(false)
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        chart.setViewPortOffsets(10f, 0f, 10f, 0f)
        chart.legend.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisLeft.spaceTop = 40f
        chart.axisLeft.spaceBottom = 40f
        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false

        progress = view.findViewById(R.id.progress)

        view.findViewById<View>(R.id.selected_currency).setOnClickListener {
            openCurrencyDialog()
        }

        view.findViewById<View>(R.id.retry_button).setOnClickListener {
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            pairContainer.isVisible = !it
            progress.isVisible = it
            chart.isVisible = !it
            errorText.isVisible = false
            retryButton.isVisible = false
        }

        viewModel.rates.observe(viewLifecycleOwner) {
            chart.clear()
            val entries =  it.withIndex().map { indexedValue ->
                Entry(
                    indexedValue.index.toFloat(),
                    indexedValue.value.value.toFloat()
                )
            }
            val set = LineDataSet(entries, "data-set")
            set.lineWidth = 1f
            set.circleRadius = 1f
            set.circleHoleRadius = 1f
            set.color = Color.BLACK
            set.setCircleColor(Color.RED)
            set.highLightColor = Color.BLACK
            set.setDrawValues(false)
            chart.data = LineData(set)
        }

        viewModel.selectedPair.observe(viewLifecycleOwner) {
            pairSymbols.text = it.symbol
            pairName.text = it.name
        }

        viewModel.error.observe(viewLifecycleOwner) {
            pairContainer.isVisible = false
            progress.isVisible = false
            chart.isVisible = false
            errorText.isVisible = true
            errorText.text = it
            retryButton.isVisible = true
        }
    }

    private fun openCurrencyDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setItems(viewModel.pairs.map { it.symbol }.toTypedArray()) { _, index ->
                val selectedCurrency = viewModel.pairs[index]
                viewModel.onCurrencyPairChanged(selectedCurrency)
            }.show()
    }
}
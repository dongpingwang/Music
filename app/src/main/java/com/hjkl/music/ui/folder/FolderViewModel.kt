package com.hjkl.music.ui.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hjkl.comm.d
import com.hjkl.entity.Folder
import com.hjkl.music.ui.comm.CommViewModel
import com.hjkl.query.parseFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FolderViewModel : CommViewModel<Folder>() {

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FolderViewModel() as T
            }
        }
    }

    init {
        "init".d()
        initFolderSource()
    }

    private fun initFolderSource() {
        "initFolderSource".d()
        viewModelScope.launch(Dispatchers.IO) {
            source().songDataSourceState.collect { source ->
                "songDataSourceState changed: ${source.shortLog()}".d()
                viewModelState.update {
                    it.copy(
                        isLoading = source.isLoading,
                        errorMsg = source.errorMsg,
                        datas = source.songs.parseFolder(),
                        updateTimeMillis = source.updateTimeMillis
                    )
                }
            }
        }
    }

}


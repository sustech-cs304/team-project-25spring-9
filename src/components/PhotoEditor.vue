<template>
  <div class="photo-editor-modal pl-60">
    <CardBox class="photo-editor-container flex flex-col w-4/5 h-4/5"> 
      <div id="image-editor" class="flex-1 overflow-hidden" ></div>
      <div class="flex justify-end gap-2 border-t px-6 py-4 bg-gray-50">
        <BaseButton label="Cancel" color="whiteDark" @click="closeEditor" />
        <BaseButton label="Save" color="success" @click="savePhoto" />
      </div>
    </CardBox>

  </div>
</template>

<style scoped>

  .photo-editor-modal {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>

<script setup>
import { ref, onMounted, watch } from 'vue'
import "tui-image-editor/dist/tui-image-editor.css"
import "tui-color-picker/dist/tui-color-picker.css"
import ImageEditor from "tui-image-editor";
import BaseButton from '@/components/BaseButton.vue'

const props = defineProps({
  photo: Object,
})

const emit = defineEmits(['save', 'close'])

const editorTheme = { 
  'common.bi.image': '',  
  'common.bisize.width': '0px',
  'common.bisize.height': '0px',
  'downloadButton.display': 'none', 
  'loadButton.display': 'none',
}

let editor = null

onMounted(() => {
  if (props.photo) {
    editor = new ImageEditor(
      document.querySelector('#image-editor'), {
      includeUI: {
        loadImage: {
          path: props.photo.src,
          name: props.photo.name,
        },
        theme: editorTheme,
        menuBarPosition: 'bottom',
      },
      usageStatistics: false,

    })
  }
})

const savePhoto = () => {
  const editedImage = editor.toDataURL();
  emit('save', { ...props.photo, src: editedImage });
}

const closeEditor = () => {
  if (editor) {
    editor.destroy();
    editor = null;
  }
  emit('close');
}

watch(() => props.photo, (newPhoto) => {
  if (editor) {
    editor.loadImageFromURL(newPhoto.src, newPhoto.name);
  }
})

</script>
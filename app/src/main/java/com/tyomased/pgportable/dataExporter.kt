//import android.graphics.Paint
//import android.graphics.Typeface
//import android.graphics.pdf.PdfDocument

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.itextpdf.io.font.FontProgramFactory
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName.BaseFont
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph


fun writeToTextFileScopedStorage(context: Context, fileName: String, content: String) {
    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val existingUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(fileName)
        val cursor = resolver.query(existingUri, null, selection, selectionArgs, null)

        if (cursor != null && cursor.moveToFirst()) {
            val existingUri =
                ContentUris.withAppendedId(existingUri,
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
            )
            resolver.openOutputStream(existingUri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
                Log.d("FileUtil", "File updated at: $existingUri")
            }
        } else {
            val uri = resolver.insert(existingUri, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                    Log.d("FileUtil", "File written to: $it")
                }
            }
        }

        cursor?.close()
    } catch (e: Exception) {
        Log.e("FileUtil", "Error writing to file: $e")
    }
}

fun writeToPDFScopedStorage(context: Context, fileName: String, content: String) {
    try {
        val resolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        // Check if the file already exists
        val existingUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(fileName)
        val cursor = resolver.query(existingUri, null, selection, selectionArgs, null)

        val pdfWriter = PdfWriter(cursor?.use {
            if (it.moveToFirst()) {
                ContentUris.withAppendedId(
                    existingUri,
                    it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                )
            } else {
                resolver.insert(existingUri, contentValues)!!
            }
        }?.let { resolver.openOutputStream(it) })

        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val FONT_FILENAME = "font/arial.ttf"

        val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)

        val paragraph = Paragraph(content)
            .setFont(font)
            .setFontSize(12F)

        document.add(paragraph)

        document.close()

        Log.d("FileUtil", "File written to: $existingUri")
    } catch (e: Exception) {
        Log.e("FileUtil", "Error writing to file: $e")
    }
}

import Foundation
import Capacitor

enum DocumentTypes: String {
    case pdf = "pdf"
    case image = "image"
    case all = "all"

    var uti: String {
        switch self {
            case .pdf: return "com.adobe.pdf"
            case .image: return "public.image"
            case .all: return "public.data"
        }
    }
}

@objc(FileSelector)
public class FileSelector: CAPPlugin {
    weak var viewController: UIViewController?;
    
    @objc func chooser(_ call: CAPPluginCall) {
//        let value = call.getArray("type", DocumentTypes.self);
        callPicker(withTypes: [DocumentTypes.all])
    }
    
    func callPicker(withTypes documentTypes: [DocumentTypes]) {

        let utis = documentTypes.map({return $0.uti })

        let picker = UIDocumentPickerViewController(documentTypes: utis, in: .import)
        picker.delegate = self
        DispatchQueue.main.async {
         self.bridge.viewController.present(picker, animated: true, completion: nil)
        }

    }

    func documentWasSelected(document: URL) {
        document.absoluteString
    }

    func sendError(_ message: String) {

    }

}

extension FileSelector: UIDocumentPickerDelegate {

    @available(iOS 11.0, *)
    public func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        if let url = urls.first {
            documentWasSelected(document: url)
        }
    }


    public func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentAt url: URL){
        documentWasSelected(document: url)
    }

    public func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        sendError("User canceled.")
    }

}

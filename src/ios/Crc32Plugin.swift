import Foundation


@objc(Crc32Plugin) class Crc32Plugin : CDVPlugin {
    @objc(crc32:) func crc32(_ command: CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            self._crc32(command)
        }
    }

    func _crc32(_ command: CDVInvokedUrlCommand) {
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)
        let urlString = command.arguments[0] as! String

        let documentsUrl =  FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first as URL!
        let destinationUrl = documentsUrl?.appendingPathComponent((urlString as NSString).lastPathComponent)
        
        let data = FileManager.default.contents(atPath: destinationUrl!.absoluteString.replacingOccurrences(of: "file://", with: ""))
        
        guard data != nil else {
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Unable to read content from \(destinationUrl?.absoluteString)"
            )
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
            return
        }

        let crc = CRC32(data: data!)
        let crcCode = String(format: "%2X", crc.hashValue)

        pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: crcCode
        )
        
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command.callbackId
        )
    }
}

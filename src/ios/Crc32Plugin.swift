import Foundation


@objc(Crc32Plugin) class Crc32Plugin : CDVPlugin {
    func crc32(_ command: CDVInvokedUrlCommand) {
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR)

        let url = URL(string: command.arguments[0] as! String)!

        print(url)


        var content: String

        do {
            content = try String(contentsOf: url, encoding: .utf8)
        }
        catch {
            print(error)
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Failed to open file at \(url)"
            )
            return
        }

        let crc = CRC32()
        crc.run(data: content.data(using: .utf8)!)
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

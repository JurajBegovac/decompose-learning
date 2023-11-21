import SwiftUI
import shared

struct MainView: View {
    private let component: MainComponent
    
    @StateValue
    private var model: MainComponentState
    
    init(_ component: MainComponent) {
        self.component = component
        _model = StateValue(component.state)
    }
    
    var body: some View {
        VStack {
            Button(action: component.onShowWelcomeClicked) {
                Text(model.buttonText)
            }.disabled(!model.buttonEnabled)
        }
        .navigationBarTitle("Decompose Template", displayMode: .inline)
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView(PreviewMainComponent())
    }
}

class PreviewMainComponent : MainComponent {
    let state: Value<MainComponentState> = mutableValue(
        MainComponentState(buttonText: "", buttonEnabled: false)
    )
    
    func onShowWelcomeClicked() {}
}

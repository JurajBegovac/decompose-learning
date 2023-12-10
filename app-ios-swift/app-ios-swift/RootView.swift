import SwiftUI
import shared

struct RootView: View {
    private let root: RootComponent

    init(_ root: RootComponent) {
        self.root = root
    }
    
    var body: some View {
        StackView(
            stackValue: StateValue(root.stack),
            getTitle: {
                let rootComponentChild = $0 as RootComponentChild
                switch onEnum(of: rootComponentChild) {
                case .main(_):
                    return "Decompose Template"
                case .welcome(_):
                    return "Welcome Screen"
                }
            },
            onBack: root.onBackClicked
        ) {
            let rootComponentChild = $0 as RootComponentChild
            switch onEnum(of: rootComponentChild) {
            case .main(let child):
                MainView(child.component)
            case .welcome(let child):
                WelcomeView(child.component)
            }
        }
    }
}

struct RootView_Previews: PreviewProvider {
    static var previews: some View {
        RootView(PreviewRootComponent())
    }
}

class PreviewRootComponent : RootComponent {
    func onBackClicked(toIndex: Int32) {}

    var stack: Value<ChildStack<AnyObject, RootComponentChild>> = simpleChildStack(RootComponentChild.Main(component: PreviewMainComponent()))
}

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Main {
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static void main(String[] args) {
        compositeDisposable.add(
                RxServer.INSTANCE.openSocket(11111)
                        .flatMap(socket -> RxServer.INSTANCE.clientListener(socket)
                                .flatMap(clientLink -> RxServer.INSTANCE.openStream(clientLink))
                                .retryWhen(throwable -> throwable.flatMap(thr -> Observable.just(socket))))
                        .subscribe(subscriber -> System.out.println("Link opened..!"),
                                throwable -> System.out.println(throwable.toString())));
        compositeDisposable.clear();
    }
}

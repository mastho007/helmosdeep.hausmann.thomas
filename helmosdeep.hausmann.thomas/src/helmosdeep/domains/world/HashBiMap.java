package helmosdeep.domains.world;

import java.util.*;

import helmosdeep.util.Contract;

/**
 * Collection représentant une correspondance bijective (un-à-un) entre des
 * valeurs de type {@code T1} et des valeurs de type {@code T2}.
 * <p>
 * Contrairement à une {@link Map} classique, chaque valeur du second type
 * n'est associée qu'à une seule valeur du premier type, et inversement :
 * il est possible de naviguer indifféremment de {@code T1} vers {@code T2}
 * ou de {@code T2} vers {@code T1}.
 * <p>
 * L'implémentation repose en interne sur deux {@link HashMap} maintenues en
 * miroir l'une de l'autre. Aucune valeur {@code null} n'est autorisée, ni
 * comme clé ni comme valeur. Contrairement à une simple {@code Map}, {@link
 * #put} refuse d'écraser une association déjà existante : il faut passer
 * explicitement par {@link #removeFirst} ou {@link #replaceFirst} pour
 * modifier une paire existante, ce qui garantit qu'aucune bijectivité ne
 * peut être rompue silencieusement.
 * <p>
 * Cette classe n'est pas synchronisée : son utilisation depuis plusieurs
 * threads sans synchronisation externe n'est pas garantie être sûre.
 *
 * @param <T1> le type des éléments du premier ensemble
 * @param <T2> le type des éléments du second ensemble
 */
public final class HashBiMap<T1, T2> {
	private final Map<T1, T2> firsts = new HashMap<>();
	private final Map<T2, T1> seconds = new HashMap<>();

	/**
	 * Retourne l'ensemble des éléments du premier type actuellement présents
	 * dans la collection.
	 * <p>
	 * L'ensemble retourné est une copie indépendante : les modifications
	 * ultérieures de la {@code HashBiMap} n'y sont pas répercutées, et
	 * inversement.
	 *
	 * @return une vue (copiée) des éléments du premier type
	 */
	public Iterable<T1> getFirsts() {
		return Collections.unmodifiableCollection(firsts.keySet());
	}

	/**
	 * Retourne l'ensemble des éléments du second type actuellement présents
	 * dans la collection.
	 * <p>
	 * L'ensemble retourné est une copie indépendante : les modifications
	 * ultérieures de la {@code HashBiMap} n'y sont pas répercutées, et
	 * inversement.
	 *
	 * @return une vue (copiée) des éléments du second type
	 */
	public Iterable<T2> getSeconds() {
		return Collections.unmodifiableCollection(seconds.keySet());
	}

	/**
	 * Retourne l'élément du second type associé à l'élément du premier type
	 * donné.
	 *
	 * @param key l'élément du premier type dont on cherche le correspondant
	 * @return l'élément du second type associé à {@code key}
	 * @throws IllegalArgumentException si {@code key} est {@code null} ou
	 *         absent de la collection
	 */
	public T2 getSecond(T1 key) {
		Contract.require(key != null && firsts.containsKey(key), "Arg. key non présent");
		return this.firsts.get(key);
	}

	/**
	 * Retourne l'élément du premier type associé à l'élément du second type
	 * donné.
	 *
	 * @param key l'élément du second type dont on cherche le correspondant
	 * @return l'élément du premier type associé à {@code key}
	 * @throws IllegalArgumentException si {@code key} est {@code null} ou
	 *         absent de la collection
	 */
	public T1 getFirst(T2 key) {
		Contract.require(key != null && seconds.containsKey(key), "Arg. key non présent");
		return this.seconds.get(key);
	}

	/**
	 * Retourne l'élément du premier type associé à {@code key}, ou
	 * {@code defaultValue} si {@code key} n'est pas présent dans la
	 * collection.
	 *
	 * @param key l'élément du second type dont on cherche le correspondant
	 * @param defaultValue la valeur à retourner si {@code key} est absent
	 * @return l'élément du premier type associé à {@code key}, ou
	 *         {@code defaultValue} si absent
	 * @throws NullPointerException si {@code key} est {@code null}
	 */
	public T1 getFirstOrDefault(T2 key, T1 defaultValue) {
		Objects.requireNonNull(key);

		return this.seconds.containsKey(key)
				? this.seconds.get(key) : defaultValue;
	}

	/**
	 * Retourne l'élément du second type associé à {@code key}, ou
	 * {@code defaultValue} si {@code key} n'est pas présent dans la
	 * collection.
	 *
	 * @param key l'élément du premier type dont on cherche le correspondant
	 * @param defaultValue la valeur à retourner si {@code key} est absent
	 * @return l'élément du second type associé à {@code key}, ou
	 *         {@code defaultValue} si absent
	 * @throws NullPointerException si {@code key} est {@code null}
	 */
	public T2 getSecondOrDefault(T1 key, T2 defaultValue) {
		Objects.requireNonNull(key);

		return this.firsts.containsKey(key)
				? this.firsts.get(key) : defaultValue;
	}

	/**
	 * Indique si la valeur donnée est présente parmi les éléments du premier
	 * type.
	 *
	 * @param val la valeur à rechercher
	 * @return {@code true} si {@code val} est présent, {@code false} sinon
	 */
	public boolean containsFirst(T1 val)  {
		return this.firsts.containsKey(val);
	}

	/**
	 * Indique si la valeur donnée est présente parmi les éléments du second
	 * type.
	 *
	 * @param val la valeur à rechercher
	 * @return {@code true} si {@code val} est présent, {@code false} sinon
	 */
	public boolean containsSecond(T2 val) {
		return this.seconds.containsKey(val);
	}

	/**
	 * Retourne le nombre de paires actuellement présentes dans la
	 * collection.
	 *
	 * @return le nombre de paires
	 */
	public int size() {
		return this.firsts.size();
	}

	/**
	 * Ajoute une nouvelle paire d'éléments associés.
	 * <p>
	 * Contrairement à une {@link Map#put}, cette méthode n'écrase jamais une
	 * association existante : si {@code first} ou {@code second} est déjà
	 * présent dans la collection, une exception est levée. Pour modifier une
	 * paire existante, utiliser {@link #removeFirst} puis {@link #put}, ou
	 * {@link #replaceFirst}.
	 *
	 * @param first l'élément du premier type
	 * @param second l'élément du second type
	 * @throws IllegalArgumentException si {@code first} ou {@code second}
	 *         est {@code null}, ou si l'un des deux est déjà présent dans
	 *         la collection
	 */
	public void put(T1 first, T2 second) {
		Contract.require(first != null && !firsts.containsKey(first), "Arg. first null ou déjà présent (utiliser replaceFirst ?)");
		Contract.require(second != null && !seconds.containsKey(second), "Arg. second null ou déjà présent");

		this.firsts.put(first, second);
		this.seconds.put(second, first);
	}

	/**
	 * Remplace la clé {@code oldKey} du premier type par {@code newKey},
	 * en conservant l'élément du second type qui lui était associé.
	 *
	 * @param oldKey la clé actuelle à remplacer
	 * @param newKey la nouvelle clé
	 * @throws IllegalArgumentException si {@code oldKey} est {@code null}
	 *         ou absent de la collection, ou si {@code newKey} est
	 *         {@code null} ou déjà présent dans la collection
	 */
	public void replaceFirst(T1 oldKey, T1 newKey) {
		Contract.require(oldKey != null && firsts.containsKey(oldKey), "Arg oldKey null ou absent");
		Contract.require(newKey != null && !firsts.containsKey(newKey), "Arg newKey null ou déjà présent (appeler remove avant)");

		var val = firsts.get(oldKey);
		firsts.remove(oldKey);
		firsts.put(newKey, val);
		seconds.replace(val, newKey);
	}

	/**
	 * Supprime la paire associée à la clé {@code key} du premier type, si
	 * elle existe. Ne fait rien si {@code key} n'est pas présent dans la
	 * collection.
	 *
	 * @param key la clé à supprimer
	 * @throws IllegalArgumentException si {@code key} est {@code null}
	 */
	public void removeFirst(T1 key) {
		Contract.require(key != null, "Arg oldKey null");
		if(!firsts.containsKey(key)) {
			return;
		}

		var val = firsts.get(key);
		firsts.remove(key);
		seconds.remove(val);
	}


}
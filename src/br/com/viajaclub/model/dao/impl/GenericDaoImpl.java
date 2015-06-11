package br.com.viajaclub.model.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.viajaclub.model.dao.GenericDao;


@Repository
public abstract class GenericDaoImpl<T, TipoId> implements GenericDao<T, TipoId>{
	
	
	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * Classe da Entidade que serï¿½ manipulada
	 */
	private Class<T> entity;
	
	public GenericDaoImpl(){
	}
	
	public GenericDaoImpl(Class<T> classe){
		this.entity = classe;
	}
	
	public GenericDaoImpl(Class<T> classe, Integer id){
		this.entity = classe;
	}
	
	
	/**
	 * 
	 * @param entidade
	 * @return
	 */
	public T merge(T entidade) {
		try {
			removeEspacoADireita(entidade);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return this.entityManager.merge(entidade);
	}

	/**
	 * Exclui uma entidade do banco de dados.
	 * 
	 * @param entidade a ser excluida
	 */
	public void excluir(T entidade) {
		this.entityManager.remove(entidade);
	}

	/**
	 * Obtem o gerenciador de entidades, caso seja necessï¿½rio 
	 * a um repositï¿½rio utilizar funï¿½ï¿½es nï¿½o existentes no 
	 * repositï¿½rio genï¿½rico.
	 * 
	 * @return EntityManager
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Obtï¿½m uma instï¿½ncia da entidade, buscada do banco de 
	 * dados a partir de sua chave primï¿½ria
	 * 
	 * @param id - valor da chave da tabela
	 * @return instï¿½ncia da entidade
	 */
	public T obterPorId(TipoId id) {
		return this.entityManager.find( this.entity, id);
	}

	/**
	 * Obtï¿½m todas as entidades persistidas no banco de 
	 * dados.
	 * 
	 * @return
	 */
	public List<T> obterTodos() {
		
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.entity);
		
		Root<T> raiz = criteriaQuery.from(this.entity);
		CriteriaQuery<T> registros = criteriaQuery.select(raiz);
		
		TypedQuery<T> queryDeTodosOsRegistros = this.entityManager.createQuery(registros);

		return queryDeTodosOsRegistros.getResultList();
	}
	
	/**
	 * 
	 * @return
	 */
	public T obter() {
		
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.entity);
		
		Root<T> raiz = criteriaQuery.from(this.entity);
		CriteriaQuery<T> registros = criteriaQuery.select(raiz);
		
		TypedQuery<T> queryDeTodosOsRegistros = this.entityManager.createQuery(registros);

		return queryDeTodosOsRegistros.getSingleResult();
	}
	
	/**
	 * 
	 * @param order
	 * @param tipo
	 * @return
	 */
	public List<T> obterTodosOrdenado(String order, String tipo) {
		
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.entity);
		Root<T> raiz = criteriaQuery.from(this.entity);
		CriteriaQuery<T> registros = criteriaQuery.select(raiz);
		
		if(tipo!=null && tipo.equalsIgnoreCase("desc")) {
			registros.orderBy(criteriaBuilder.desc(raiz.get(order)));
		} else {
			registros.orderBy(criteriaBuilder.asc(raiz.get(order)));
		}
		
		TypedQuery<T> queryDeTodosOsRegistros = this.entityManager.createQuery(registros);
		return queryDeTodosOsRegistros.getResultList();
	}
	
	/**
	 * 
	 * @param order
	 * @param tipo
	 * @return
	 */
	public List<T> obterTodosOrdenadoPagina(String order, String tipo, int pagina) {
		
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.entity);
		Root<T> raiz = criteriaQuery.from(this.entity);
		CriteriaQuery<T> registros = criteriaQuery.select(raiz);
		
		if(tipo!=null && tipo.equalsIgnoreCase("desc")) {
			registros.orderBy(criteriaBuilder.desc(raiz.get(order)));
		} else {
			registros.orderBy(criteriaBuilder.asc(raiz.get(order)));
		}
		
		TypedQuery<T> queryDeTodosOsRegistros = this.entityManager.createQuery(registros);
		queryDeTodosOsRegistros.setFirstResult(10 * pagina - 10);
		queryDeTodosOsRegistros.setMaxResults(10);
		return queryDeTodosOsRegistros.getResultList();
	}
	
	
	public List<T> obterTodosComRelacionamentosOrdenadoPagina(String order, String tipo, int pagina, String... relacionamentos) {
		
		CriteriaBuilder construtorDeQuery = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = construtorDeQuery.createQuery(this.entity);

		Root<T> entidade = query.from(this.entity);
		
		for (String entidadeRelacionada : relacionamentos) {
			entidade.fetch(entidadeRelacionada, JoinType.LEFT);
		}
		
		if(tipo!=null && tipo.equalsIgnoreCase("desc")) {
			query.orderBy(construtorDeQuery.desc(entidade.get(order)));
		} else {
			query.orderBy(construtorDeQuery.asc(entidade.get(order)));
		}
		
		TypedQuery<T> queryDeTodosOsRegistros = this.entityManager.createQuery(query);
		queryDeTodosOsRegistros.setFirstResult(10 * pagina - 10);
		queryDeTodosOsRegistros.setMaxResults(10);
		return queryDeTodosOsRegistros.getResultList();
	}

	/**
	 * Limpa todas as instï¿½ncias de uma determinada classe do cache de segundo
	 * nï¿½vel.
	 * 
	 * @param type
	 *            - classe mapeada no cache
	 */
	public void limparTodoCacheDe(Class<T> type) {
		
		this.entityManager.getEntityManagerFactory().getCache().evict(type);
	}

	/**
	 * Limpa a instï¿½ncia de uma determinada classe do cache de segundo nï¿½vel a
	 * partir de sua chave de identificaï¿½ï¿½o.
	 * @param type - classe mapeada no cache
	 * @param chace - chave da instï¿½ncia a ser excluï¿½da do cache.
	 */
	public void limparInstanciaDoCache(Class<T> type, Object chave) {
		this.entityManager.getEntityManagerFactory().getCache().evict(type, chave);
	}

	@Override
	public List<T> obterTodosComRelacionamentos(String... relacionamentos) {
		
		CriteriaBuilder construtorDeQuery = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = construtorDeQuery.createQuery(this.entity);

		Root<T> entidade = query.from(this.entity);
		
		for (String entidadeRelacionada : relacionamentos) {
			entidade.fetch(entidadeRelacionada);
		}
		
		return this.entityManager.createQuery(query.select(entidade)).getResultList();
	}
	
	public List<T> obterTodosComRelacionamentosOrdenado(String order, String tipo, String... relacionamentos) {
		
		CriteriaBuilder construtorDeQuery = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = construtorDeQuery.createQuery(this.entity);

		Root<T> entidade = query.from(this.entity);
		
		for (String entidadeRelacionada : relacionamentos) {
			entidade.fetch(entidadeRelacionada, JoinType.LEFT);
		}
		
		if(tipo!=null && tipo.equalsIgnoreCase("desc")) {
			query.orderBy(construtorDeQuery.desc(entidade.get(order)));
		} else {
			query.orderBy(construtorDeQuery.asc(entidade.get(order)));
		}
		
		return this.entityManager.createQuery(query.select(entidade)).getResultList();
	}

	
	@Override
	public T obterPorIdComRelacionamentos(TipoId id, String... relacionamentos) {
		
		CriteriaBuilder construtorDeQuery = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = construtorDeQuery.createQuery(this.entity);

		Root<T> entidade = query.from(this.entity);

		for (String entidadeRelacionada : relacionamentos) {
			entidade.fetch(entidadeRelacionada, JoinType.LEFT);

		}
		
		Predicate condition = construtorDeQuery.equal(entidade.get(getNomeDaChavePrimaria()), id);
	    query.where(condition);
		
		return this.entityManager.createQuery(query.select(entidade)).getSingleResult();
	}
	
	@Override
	public List<T> obterComRelacionamentosPor(Map<String, Object> parametros, String... relacionamentos) {
		
		CriteriaBuilder construtorDeQuery = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = construtorDeQuery.createQuery(this.entity);

		Root<T> entidade = query.from(this.entity);

		for (String entidadeRelacionada : relacionamentos) {
			entidade.fetch(entidadeRelacionada);

		}
		
		Set<String> chaves = parametros.keySet();
		List<Predicate> condicoes = new ArrayList<Predicate>(chaves.size());
		for (String chaveDoParametro : chaves) {
			
			condicoes.add(construtorDeQuery.equal(entidade.get(chaveDoParametro), parametros.get(chaveDoParametro)));
		}
		
		query.where(condicoes.toArray(new Predicate[]{}));
		
		return this.entityManager.createQuery(query.select(entidade)).getResultList();
	}
	
	/**
	 * 
	 * @param entidade
	 * @param nmAttribute
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@SuppressWarnings("unchecked")
	public List<T> listarByAttribute(T entidade, String nmAttribute) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException{
		
		List<T> lista = new ArrayList<T>();
		
		StringBuilder consulta = new StringBuilder();
		
		consulta.append(" select ");
		consulta.append(StringUtils.lowerCase(nmAttribute));
		consulta.append(", count(*) from ");
		consulta.append(StringUtils.lowerCase(entidade.getClass().getSimpleName()));
		consulta.append(" group by ");
		consulta.append(StringUtils.lowerCase(nmAttribute));
		consulta.append(" order by ");
		consulta.append(StringUtils.lowerCase(nmAttribute));
		
		Query procedure = this.getEntityManager().createNativeQuery(consulta.toString());
		List<Object[]> tmpList = procedure.getResultList();
		
		Class<?> classe = Class.forName(entidade.getClass().getName());
		
		for(Object[] obj : tmpList) {
			
			T cE = (T) classe.newInstance();
			
			Field id = cE.getClass().getDeclaredField(nmAttribute);
			id.setAccessible(true);
			id.set(cE, (String)obj[0]);
			
			lista.add(cE);
		}
		
		return lista;
	}
	
	/**
	 * Funï¿½ï¿½o auxiliar para obter o nome do atributo da classe que ï¿½ a chave primï¿½ria, ou seja, possui a anotaï¿½ï¿½o @Id.
	 * Isto ï¿½ utilizado na funï¿½ï¿½o {@link  obterPorIdComRelacionamentos}
	 *  
	 * @return
	 */
	private String getNomeDaChavePrimaria(){
		Field[] fields = this.entity.getDeclaredFields();
		
		for (Field field : fields) {
			
			if(field.getAnnotation(Id.class) != null || field.getAnnotation(EmbeddedId.class) != null)
				return field.getName();
		}
		
		return "id";
	}
	
	/**
	 * Funï¿½ï¿½o auxiliar para obter os atributo da classe que ï¿½ do tipo String e remover os espaços a direita.
	 *  
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	private void removeEspacoADireita(T entidade) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Field[] fields = entidade.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			
			if(field.getType().equals(String.class)) {
				field.setAccessible(true);
				Object value = field.get(entidade);
				
				if(value != null)
					field.set(entidade, value.toString().trim());
			}
		}
	}
}
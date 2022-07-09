package mcjty.lostcities.worldgen.lost.cityassets;

import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.api.ILostCityAssetRegistry;
import mcjty.lostcities.worldgen.lost.regassets.data.DataTools;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RegistryAssetRegistry<T extends ILostCityAsset, R extends IForgeRegistryEntry<R>> implements ILostCityAssetRegistry<T>  {

    private final Map<ResourceLocation, T> assets = new HashMap<>();
    private final ResourceKey<Registry<R>> registryKey;
    private final Function<R, T> assetConstructor;

    public <S extends ILostCityAsset> ILostCityAssetRegistry<S> cast() {
        return (ILostCityAssetRegistry<S>) this;
    }

    public RegistryAssetRegistry(ResourceKey<Registry<R>> registryKey, Function<R, T> assetConstructor) {
        this.registryKey = registryKey;
        this.assetConstructor = assetConstructor;
    }

    @Override
    public T get(CommonLevelAccessor level, String name) {
        if (name == null) {
            return null;
        }
        return get(level, DataTools.fromName(name));
    }

    @Override
    public T get(CommonLevelAccessor level, ResourceLocation name) {
        if (name == null) {
            return null;
        }
        T t = assets.get(name);
        if (t == null) {
            Registry<R> registry = level.registryAccess().registryOrThrow(registryKey);
            R value = registry.get(ResourceKey.create(registryKey, name));
            value.setRegistryName(name);
            t = assetConstructor.apply(value);
            assets.put(name, t);
        }
        if (t != null) {
            t.init(level);
        }
        return t;
    }

    @Override
    public Iterable<T> getIterable() {
        return assets.values();
    }

    public void reset() {
        assets.clear();
    }
}
